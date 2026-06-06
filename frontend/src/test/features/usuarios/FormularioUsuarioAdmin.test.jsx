import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import FormularioUsuarioAdmin from '../../../features/usuarios/components/FormularioUsuarioAdmin';

const estudiantesBase = [
  { id: 'u1', nombres: 'Carlos', apellidos: 'Pérez', rut: '11111111-1' },
];

const usuarioEditandoBase = {
  id: 99,
  rut: '12345678-9',
  nombres: 'Ana',
  apellidos: 'García',
  email: 'ana@test.cl',
  rol: 'DOCENTE',
};

function llenarCamposCreacion(overrides = {}) {
  const datos = {
    rut: '12345678-9',
    nombres: 'Juan',
    apellidos: 'López',
    email: 'juan@test.cl',
    password: 'Pass123!',
    confirmPassword: 'Pass123!',
    rol: 'DOCENTE',
    ...overrides,
  };
  if (datos.rut)             fireEvent.change(screen.getByLabelText('RUT'),                   { target: { name: 'rut',             value: datos.rut } });
  if (datos.nombres)         fireEvent.change(screen.getByLabelText('Nombres'),               { target: { name: 'nombres',         value: datos.nombres } });
  if (datos.apellidos)       fireEvent.change(screen.getByLabelText('Apellidos'),             { target: { name: 'apellidos',       value: datos.apellidos } });
  if (datos.email)           fireEvent.change(screen.getByLabelText('Correo electrónico'),    { target: { name: 'email',           value: datos.email } });
  if (datos.password)        fireEvent.change(screen.getByLabelText('Contraseña'),            { target: { name: 'password',        value: datos.password } });
  if (datos.confirmPassword) fireEvent.change(screen.getByLabelText('Confirmar contraseña'), { target: { name: 'confirmPassword', value: datos.confirmPassword } });
  if (datos.rol)             fireEvent.change(screen.getByLabelText('Rol en el sistema'),     { target: { name: 'rol',             value: datos.rol } });
}

describe('FormularioUsuarioAdmin — modo creación', () => {
  it('muestra título "Nuevo Usuario del Sistema"', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} />);
    expect(screen.getByText('Nuevo Usuario del Sistema')).toBeInTheDocument();
  });

  it('muestra botón "Crear usuario"', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} />);
    expect(screen.getByRole('button', { name: 'Crear usuario' })).toBeInTheDocument();
  });

  it('no muestra botón cancelar en modo creación', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} />);
    expect(screen.queryByText('Cancelar')).not.toBeInTheDocument();
  });

  it('muestra errores de validación al enviar vacío', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} />);
    fireEvent.click(screen.getByRole('button', { name: 'Crear usuario' }));
    const errores = screen.getAllByRole('alert');
    expect(errores.length).toBeGreaterThan(0);
  });

  it('muestra error cuando las contraseñas no coinciden', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} />);
    llenarCamposCreacion({ password: 'Pass123!', confirmPassword: 'Diferente!' });
    fireEvent.click(screen.getByRole('button', { name: 'Crear usuario' }));
    expect(screen.getByText('Las contraseñas no coinciden.')).toBeInTheDocument();
  });

  it('llama a onGuardar con los datos correctos al enviar válido', () => {
    const onGuardar = vi.fn();
    render(<FormularioUsuarioAdmin onGuardar={onGuardar} />);
    llenarCamposCreacion();
    fireEvent.click(screen.getByRole('button', { name: 'Crear usuario' }));
    expect(onGuardar).toHaveBeenCalledWith(expect.objectContaining({
      rut: '12345678-9',
      nombres: 'Juan',
      rol: 'DOCENTE',
    }));
  });

  it('muestra selector de pupilo cuando el rol es APODERADO', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} estudiantes={estudiantesBase} />);
    fireEvent.change(screen.getByLabelText('Rol en el sistema'), { target: { name: 'rol', value: 'APODERADO' } });
    expect(screen.getByLabelText('Pupilo asignado')).toBeInTheDocument();
  });

  it('no muestra selector de pupilo cuando el rol no es APODERADO', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} />);
    expect(screen.queryByLabelText('Pupilo asignado')).not.toBeInTheDocument();
  });

  it('muestra error de pupilo si rol es APODERADO y no se selecciona pupilo', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} estudiantes={estudiantesBase} />);
    llenarCamposCreacion({ rol: 'APODERADO' });
    fireEvent.change(screen.getByLabelText('Rol en el sistema'), { target: { name: 'rol', value: 'APODERADO' } });
    fireEvent.click(screen.getByRole('button', { name: 'Crear usuario' }));
    expect(screen.getByText('Debes asignar un pupilo al apoderado.')).toBeInTheDocument();
  });
});

describe('FormularioUsuarioAdmin — modo edición', () => {
  it('muestra título "Editar Usuario"', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} usuarioEditando={usuarioEditandoBase} onCancelar={() => {}} />);
    expect(screen.getByText('Editar Usuario')).toBeInTheDocument();
  });

  it('muestra botón "Guardar cambios"', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} usuarioEditando={usuarioEditandoBase} onCancelar={() => {}} />);
    expect(screen.getByRole('button', { name: 'Guardar cambios' })).toBeInTheDocument();
  });

  it('muestra botón cancelar en modo edición', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} usuarioEditando={usuarioEditandoBase} onCancelar={() => {}} />);
    expect(screen.getByText('Cancelar')).toBeInTheDocument();
  });

  it('llama a onCancelar al hacer click en cancelar', () => {
    const onCancelar = vi.fn();
    render(<FormularioUsuarioAdmin onGuardar={() => {}} usuarioEditando={usuarioEditandoBase} onCancelar={onCancelar} />);
    fireEvent.click(screen.getByText('Cancelar'));
    expect(onCancelar).toHaveBeenCalled();
  });

  it('precarga los datos del usuario en el formulario', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} usuarioEditando={usuarioEditandoBase} onCancelar={() => {}} />);
    expect(screen.getByDisplayValue('12345678-9')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Ana')).toBeInTheDocument();
    expect(screen.getByDisplayValue('ana@test.cl')).toBeInTheDocument();
  });

  it('el select de rol está deshabilitado en modo edición', () => {
    render(<FormularioUsuarioAdmin onGuardar={() => {}} usuarioEditando={usuarioEditandoBase} onCancelar={() => {}} />);
    expect(screen.getByLabelText('Rol en el sistema')).toBeDisabled();
  });
});
