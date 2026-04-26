from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession
from pydantic import BaseModel, ConfigDict
from datetime import datetime, timedelta, timezone, date
from typing import Optional
import bcrypt
from jose import jwt, JWTError
import os
from dotenv import load_dotenv

from database import engine, get_db
from models import Usuario, Instrumento
import schemas

# --- Configuración ---
load_dotenv()
SECRET_KEY = os.getenv("JWT_SECRET", "clave-secreta-defecto-cambia-en-prod")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 1440

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="login")

# --- App FastAPI ---
app = FastAPI(title="API Instrumentación Refinería", version="1.2.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- Modelos Pydantic para Login ---
class LoginRequest(BaseModel):
    user_name: str
    password: str

class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"

# --- Funciones de Seguridad ---
def verify_password(plain: str, hashed: str) -> bool:
    return bcrypt.checkpw(plain.encode("utf-8"), hashed.encode("utf-8"))

def hash_password(plain: str) -> str:
    return bcrypt.hashpw(plain.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")

def create_access_token(data: dict) -> str:
    to_encode = data.copy()
    expire = datetime.now(timezone.utc) + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

# --- Dependencias de Autenticación ---
async def get_current_user(token: str = Depends(oauth2_scheme), db: AsyncSession = Depends(get_db)):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Credenciales inválidas",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        rol: str = payload.get("rol")
        if username is None or rol is None:
            raise credentials_exception
    except JWTError:
        raise credentials_exception
    
    result = await db.execute(select(Usuario).where(Usuario.user_name == username))
    user = result.scalars().first()
    if user is None:
        raise credentials_exception
    return user

def get_current_admin(current_user: Usuario = Depends(get_current_user)):
    if current_user.rol != "admin":
        raise HTTPException(status_code=403, detail="Permisos insuficientes. Se requiere rol 'admin'")
    return current_user

# --- Rutas Públicas ---
@app.get("/")
def read_root():
    return {"msg": "API corriendo. Ve a /docs para probar endpoints."}

@app.post("/login", response_model=TokenResponse)
async def login(
    login_data: LoginRequest,
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(select(Usuario).where(Usuario.user_name == login_data.user_name))
    user = result.scalars().first()
    
    if not user or not verify_password(login_data.password, user.password):
        raise HTTPException(status_code=401, detail="Usuario o contraseña incorrectos")
    
    user.last_login = datetime.now(timezone.utc)
    await db.commit()
    
    token = create_access_token(data={"sub": user.user_name, "rol": user.rol})
    return TokenResponse(access_token=token)

# ==========================================
# CRUD USUARIOS (Solo Admin)
# ==========================================

@app.post("/usuarios", response_model=schemas.UsuarioResponse, status_code=201)
async def crear_usuario(
    data: schemas.UsuarioCreate,
    admin: Usuario = Depends(get_current_admin),
    db: AsyncSession = Depends(get_db)
):
    exists = await db.execute(select(Usuario).where(Usuario.user_name == data.user_name))
    if exists.scalars().first():
        raise HTTPException(status_code=400, detail="El nombre de usuario ya existe")
    
    nuevo = Usuario(
        user_name=data.user_name,
        password=hash_password(data.password),
        rol=data.rol
    )
    db.add(nuevo)
    await db.commit()
    await db.refresh(nuevo)
    return nuevo

@app.get("/usuarios", response_model=list[schemas.UsuarioResponse])
async def listar_usuarios(
    admin: Usuario = Depends(get_current_admin),
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(select(Usuario).order_by(Usuario.id))
    return result.scalars().all()

@app.get("/usuarios/{user_id}", response_model=schemas.UsuarioResponse)
async def obtener_usuario(
    user_id: int,
    admin: Usuario = Depends(get_current_admin),
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(select(Usuario).where(Usuario.id == user_id))
    user = result.scalars().first()
    if not user:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    return user

@app.put("/usuarios/{user_id}", response_model=schemas.UsuarioResponse)
async def actualizar_usuario(
    user_id: int,
    data: schemas.UsuarioUpdate,
    admin: Usuario = Depends(get_current_admin),
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(select(Usuario).where(Usuario.id == user_id))
    user = result.scalars().first()
    if not user:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    
    if data.rol is not None:
        user.rol = data.rol
    if data.password is not None:
        user.password = hash_password(data.password)
    
    await db.commit()
    await db.refresh(user)
    return user

@app.delete("/usuarios/{user_id}", status_code=200)
async def eliminar_usuario(
    user_id: int,
    admin: Usuario = Depends(get_current_admin),
    db: AsyncSession = Depends(get_db)
):
    result = await db.execute(select(Usuario).where(Usuario.id == user_id))
    user = result.scalars().first()
    if not user:
        raise HTTPException(status_code=404, detail="Usuario no encontrado")
    
    if user.rol == "admin":
        admins = await db.execute(select(Usuario).where(Usuario.rol == "admin"))
        if len(admins.scalars().all()) <= 1:
            raise HTTPException(status_code=400, detail="No puedes eliminar al único administrador")
    
    await db.delete(user)
    await db.commit()
    return {"msg": f"Usuario {user.user_name} eliminado correctamente"}

# ==========================================
# CRUD INSTRUMENTOS
# ==========================================

@app.get("/instrumentos", response_model=list[schemas.InstrumentoResponse])
async def get_instrumentos(
    tag: Optional[str] = None,
    planta: Optional[str] = None,
    instrumento: Optional[str] = None,
    tarjeta: Optional[str] = None,
    dir_im: Optional[str] = None,
    dir_pa: Optional[str] = None,
    var_medida: Optional[str] = None,
    comunicacion: Optional[str] = None,
    db: AsyncSession = Depends(get_db),
    current_user: Usuario = Depends(get_current_user)
):
    stmt = select(Instrumento)
    if tag:
        stmt = stmt.where(Instrumento.tag.ilike(f"%{tag}%"))
    if planta:
        stmt = stmt.where(Instrumento.planta == planta)
    if instrumento:
        stmt = stmt.where(Instrumento.instrumento == instrumento)
    if tarjeta:
        stmt = stmt.where(Instrumento.tarjeta.ilike(f"%{tarjeta}%"))
    if dir_im:
        stmt = stmt.where(Instrumento.dir_im.ilike(f"%{dir_im}%"))
    if dir_pa:
        stmt = stmt.where(Instrumento.dir_pa.ilike(f"%{dir_pa}%"))
    if var_medida:
        stmt = stmt.where(Instrumento.var_medida == var_medida)
    if comunicacion:
        stmt = stmt.where(Instrumento.comunicacion == comunicacion)
    
    result = await db.execute(stmt.order_by(Instrumento.tag))
    return result.scalars().all()

@app.get("/instrumentos/filtros/distintos")
async def get_distinct_values(
    db: AsyncSession = Depends(get_db),
    current_user: Usuario = Depends(get_current_user)
):
    try:
        fields = ["planta", "instrumento", "var_medida", "comunicacion"]
        response = {}
        for field in fields:
            column = getattr(Instrumento, field)
            stmt = select(column).distinct().where(column != None)
            result = await db.execute(stmt)
            values = result.scalars().all()
            response[field] = sorted([str(v) for v in values if v])
        return response
    except Exception as e:
        print(f"Error in distinct_values: {e}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/instrumentos/{instrumento_id}", response_model=schemas.InstrumentoResponse)
async def get_instrumento(
    instrumento_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: Usuario = Depends(get_current_user)
):
    stmt = select(Instrumento).where(Instrumento.id == instrumento_id)
    result = await db.execute(stmt)
    instrumento = result.scalars().first()
    if not instrumento:
        raise HTTPException(status_code=404, detail="Instrumento no encontrado")
    return instrumento

@app.post("/instrumentos", response_model=schemas.InstrumentoResponse, status_code=201)
async def create_instrumento(
    instrumento: schemas.InstrumentoCreate,
    db: AsyncSession = Depends(get_db),
    current_user: Usuario = Depends(get_current_admin)
):
    try:
        # 1. Verificar si el tag ya existe
        stmt = select(Instrumento).where(Instrumento.tag == instrumento.tag)
        result = await db.execute(stmt)
        if result.scalars().first():
            raise HTTPException(status_code=400, detail=f"El tag '{instrumento.tag}' ya existe")

        # 2. Preparar datos: excluir campos no enviados y agregar fecha_update
        data = instrumento.model_dump(exclude_unset=True)
        data["fecha_update"] = datetime.now(timezone.utc)
        
        # 3. Crear instancia del modelo
        nuevo = Instrumento(**data)
        
        # 4. Guardar en BD
        db.add(nuevo)
        await db.commit()
        await db.refresh(nuevo)
        return nuevo
        
    except HTTPException:
        await db.rollback()
        raise
    except Exception as e:
        await db.rollback()
        print(f"❌ ERROR POST /instrumentos: {type(e).__name__} - {str(e)}")
        raise HTTPException(status_code=400, detail=f"Error: {str(e)}")
@app.put("/instrumentos/{instrumento_id}", response_model=schemas.InstrumentoResponse)
async def update_instrumento(
    instrumento_id: int,
    instrumento: schemas.InstrumentoUpdate,
    db: AsyncSession = Depends(get_db),
    current_user: Usuario = Depends(get_current_admin)
):
    stmt = select(Instrumento).where(Instrumento.id == instrumento_id)
    result = await db.execute(stmt)
    existing = result.scalars().first()
    if not existing:
        raise HTTPException(status_code=404, detail="Instrumento no encontrado")

    update_data = instrumento.model_dump(exclude_unset=True)
    for key, value in update_data.items():
        setattr(existing, key, value)
    
    existing.fecha_update = datetime.now(timezone.utc)
    await db.commit()
    await db.refresh(existing)
    return existing

@app.delete("/instrumentos/{instrumento_id}", status_code=200)
async def delete_instrumento(
    instrumento_id: int,
    db: AsyncSession = Depends(get_db),
    current_user: Usuario = Depends(get_current_admin)
):
    stmt = select(Instrumento).where(Instrumento.id == instrumento_id)
    result = await db.execute(stmt)
    existing = result.scalars().first()
    if not existing:
        raise HTTPException(status_code=404, detail="Instrumento no encontrado")

    await db.delete(existing)
    await db.commit()
    return {"msg": f"Instrumento {existing.tag} eliminado"}
