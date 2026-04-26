from sqlalchemy import Column, Integer, String, DateTime, Date, func
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class Usuario(Base):
    __tablename__ = "usuarios"

    id = Column(Integer, primary_key=True, index=True)
    user_name = Column(String(50), unique=True, nullable=False, index=True)
    password = Column(String(255), nullable=False)
    rol = Column(String(20), nullable=False)
    last_login = Column(DateTime(timezone=True), nullable=True)

class Instrumento(Base):
    __tablename__ = "instrumentacion"

    id = Column(Integer, primary_key=True, index=True)
    planta = Column(String(5), nullable=True)
    tag = Column(String(50), nullable=False, index=True)
    dir_im = Column(String(5), nullable=True)
    dir_pa = Column(String(5), nullable=True)
    tarjeta = Column(String(50), nullable=True)
    var_medida = Column(String(50), nullable=True)
    instrumento = Column(String(50), nullable=True)
    comunicacion = Column(String(10), nullable=True)
    seguridad = Column(String(5), nullable=True)
    descripcion = Column(String(255), nullable=True)
    fecha_update = Column(DateTime(timezone=True), nullable=True)
    user_update = Column(String(50), nullable=True)
    low_warning = Column(Integer, nullable=True)
    high_warning = Column(Integer, nullable=True)
    low_alarm = Column(Integer, nullable=True)
    high_alarm = Column(Integer, nullable=True)
    start_wr = Column(Integer, nullable=True)
    end_wr = Column(Integer, nullable=True)
    start_mr = Column(Integer, nullable=True)
    end_mr = Column(Integer, nullable=True)
    no_serie = Column(String(50), nullable=True)
    rango = Column(String(50), nullable=True)
    alta_calib = Column(Date, nullable=True)       # ✅ Ahora Date está importado
    baja_calib = Column(Date, nullable=True)       # ✅
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=True)
