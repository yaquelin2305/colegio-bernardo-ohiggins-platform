import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import TablaUsuarios from '../../../features/usuarios/components/TablaUsuarios';

const columnas = ['RUT', 'Nombre', 'Email'];
const listaBase = [
  { id: 1, rut: '12345678-9', nombres: 'Ana', apellidos: 'García', email: 'ana@test.cl' },
  { id: 2, rut: '98765432-1', nombres: 'Luis', apellidos: 'Pérez', email: 'luis@test.cl' },
];

const propsBase = {
  tabActiva: 'docentes',
  columnas,
  usuarioEditando: null,
  confirmarEliminarId: null,
  onEditar: () => {},
  onEliminar: () => {},
  onToggleConfirmar: () => {},
};

describe('TablaUsuarios', () => {
  it('muestra mensaje vacío cuando la lista está vacía', () => {
    render(<TablaUsuarios {...propsBase} lista={[]} />);
    expect(screen.getByText('No hay docentes registrados.')).toBeInTheDocument();
  });

  it('muestra los datos de los usuarios', () => {
    render(<TablaUsuarios {...propsBase} lista={listaBase} />);
    expect(screen.getByText('12345678-9')).toBeInTheDocument();
    expect(screen.getByText('98765432-1')).toBeInTheDocument();
    expect(screen.getByText('ana@test.cl')).toBeInTheDocument();
    expect(screen.getByText('luis@test.cl')).toBeInTheDocument();
  });

  it('muestra el conteo de usuarios en el título', () => {
    render(<TablaUsuarios {...propsBase} lista={listaBase} />);
    expect(screen.getByText('2')).toBeInTheDocument();
  });

  it('llama a onEditar con el usuario correcto al hacer click en editar', () => {
    const onEditar = vi.fn();
    render(<TablaUsuarios {...propsBase} lista={listaBase} onEditar={onEditar} />);
    fireEvent.click(screen.getByLabelText('Editar Ana'));
    expect(onEditar).toHaveBeenCalledWith(listaBase[0]);
  });

  it('llama a onToggleConfirmar con el id al hacer click en eliminar', () => {
    const onToggleConfirmar = vi.fn();
    render(<TablaUsuarios {...propsBase} lista={listaBase} onToggleConfirmar={onToggleConfirmar} />);
    fireEvent.click(screen.getByLabelText('Eliminar Ana'));
    expect(onToggleConfirmar).toHaveBeenCalledWith(1);
  });

  it('muestra fila de confirmación cuando confirmarEliminarId coincide', () => {
    render(<TablaUsuarios {...propsBase} lista={listaBase} confirmarEliminarId={1} />);
    expect(screen.getByText(/Eliminar a/)).toBeInTheDocument();
    expect(screen.getByText(/Sí, eliminar/)).toBeInTheDocument();
  });

  it('llama a onEliminar con el id al confirmar eliminación', () => {
    const onEliminar = vi.fn();
    render(<TablaUsuarios {...propsBase} lista={listaBase} confirmarEliminarId={1} onEliminar={onEliminar} />);
    fireEvent.click(screen.getByText(/Sí, eliminar/));
    expect(onEliminar).toHaveBeenCalledWith(1);
  });

  it('llama a onToggleConfirmar con null al cancelar eliminación', () => {
    const onToggleConfirmar = vi.fn();
    render(<TablaUsuarios {...propsBase} lista={listaBase} confirmarEliminarId={1} onToggleConfirmar={onToggleConfirmar} />);
    fireEvent.click(screen.getByText(/Cancelar/));
    expect(onToggleConfirmar).toHaveBeenCalledWith(null);
  });

  it('muestra celda de pupilo cuando tabActiva es apoderados', () => {
    const apoderado = { id: 1, rut: '11111111-1', nombres: 'María', apellidos: 'Torres', email: 'maria@test.cl', pupiloNombre: 'Carlos Torres' };
    render(<TablaUsuarios {...propsBase} lista={[apoderado]} tabActiva="apoderados" columnas={[...columnas, 'Pupilo']} />);
    expect(screen.getByText('Carlos Torres')).toBeInTheDocument();
  });

  it('muestra "—" cuando el apoderado no tiene pupilo asignado', () => {
    const apoderado = { id: 1, rut: '11111111-1', nombres: 'María', apellidos: 'Torres', email: 'maria@test.cl', pupiloNombre: null };
    render(<TablaUsuarios {...propsBase} lista={[apoderado]} tabActiva="apoderados" columnas={[...columnas, 'Pupilo']} />);
    expect(screen.getByText('—')).toBeInTheDocument();
  });
});
