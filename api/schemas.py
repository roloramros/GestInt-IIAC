from pydantic import BaseModel, ConfigDict
from datetime import datetime, date
from typing import Optional

# === USUARIOS ===
class UsuarioBase(BaseModel):
    user_name: str
    rol: str

class UsuarioCreate(UsuarioBase):
    password: str

class UsuarioUpdate(BaseModel):
    password: Optional[str] = None
    rol: Optional[str] = None

class UsuarioResponse(UsuarioBase):
    id: int
    last_login: Optional[datetime] = None
    
    model_config = ConfigDict(from_attributes=True)

# === INSTRUMENTOS ===
class InstrumentoBase(BaseModel):
    planta: Optional[str] = None
    tag: str
    dir_im: Optional[str] = None
    dir_pa: Optional[str] = None
    tarjeta: Optional[str] = None
    var_medida: Optional[str] = None
    instrumento: Optional[str] = None
    comunicacion: Optional[str] = None
    seguridad: Optional[str] = None
    descripcion: Optional[str] = None
    fecha_update: Optional[datetime] = None
    user_update: Optional[str] = None
    low_warning: Optional[int] = None
    high_warning: Optional[int] = None
    low_alarm: Optional[int] = None
    high_alarm: Optional[int] = None
    start_wr: Optional[int] = None
    end_wr: Optional[int] = None
    start_mr: Optional[int] = None
    end_mr: Optional[int] = None
    no_serie: Optional[str] = None
    rango: Optional[str] = None
    alta_calib: Optional[date] = None
    baja_calib: Optional[date] = None

class InstrumentoCreate(InstrumentoBase):
    pass

class InstrumentoUpdate(InstrumentoBase):
    pass

class InstrumentoResponse(InstrumentoBase):
    id: int
    created_at: Optional[datetime] = None
    
    model_config = ConfigDict(from_attributes=True)
