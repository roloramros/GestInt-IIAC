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

class InstrumentoCreate(InstrumentoBase):
    pass

class InstrumentoUpdate(InstrumentoBase):
    pass

class InstrumentoResponse(InstrumentoBase):
    id: int
    created_at: Optional[datetime] = None
    
    model_config = ConfigDict(from_attributes=True)

# === HISTORIAL ===
class HistorialAccesoResponse(BaseModel):
    id: int
    user_id: Optional[int] = None
    username: str
    ip_address: str
    login_time: datetime
    status: str

    model_config = ConfigDict(from_attributes=True)

# === CERTIFICADOS DE CALIBRACIÓN ===
class CertificadoBase(BaseModel):
    no_certificado: str
    id_instrumento: int
    estado_tecnico: Optional[str] = None
    observaciones: Optional[str] = None
    fecha: Optional[datetime] = None

class CertificadoCreate(CertificadoBase):
    pass

class CertificadoUpdate(BaseModel):
    no_certificado: Optional[str] = None
    id_instrumento: Optional[int] = None
    estado_tecnico: Optional[str] = None
    observaciones: Optional[str] = None
    fecha: Optional[datetime] = None

class CertificadoResponse(CertificadoBase):
    id: int
    
    model_config = ConfigDict(from_attributes=True)

class CertificadoDetailedResponse(BaseModel):
    id: int
    no_certificado: str
    estado_tecnico: Optional[str] = None
    observaciones: Optional[str] = None
    fecha: Optional[datetime] = None
    no_serie: Optional[str] = None
    instrumento: Optional[str] = None
    descripcion: Optional[str] = None
    rango: Optional[str] = None

    model_config = ConfigDict(from_attributes=True)
