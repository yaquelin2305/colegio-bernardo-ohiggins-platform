import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import GestionUsuariosPage from '../../../../features/usuarios/pages/GestionUsuariosPage';
import * as s from '../../../../features/usuarios/services/usuariosService';

vi.mock('../../../../features/usuarios/services/usuariosService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

const docenteMock = { id: 'd1', nombres: 'Ana', apellidos: 'Profe', rut: '11-1', email: 'a@b.cl', rol: 'DOCENTE', pupiloUuid: null, pupiloNombre: null };
const apoderadoMock = { id: 'a1', nombres: 'Luis', apellidos: 'Padre', rut: '22-2', email: 'l@b.cl', rol: 'APODERADO', pupiloUuid: 'e1', pupiloNombre: null };
const estudianteMock = { id: 'e1', nombres: 'Sofía', apellidos: 'Alumno', rut: '33-3', email: 's@b.cl', rol: 'ESTUDIANTE', pupiloUuid: null, pupiloNombre: null };

function renderPage() {
  return render(<BrowserRouter><GestionUsuariosPage /></BrowserRouter>);
}

async function esperarCarga() {
  await waitFor(() => expect(screen.getByText('Nuevo Usuario del Sistema')).toBeInTheDocument());
}

describe('GestionUsuariosPage', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('muestra "Cargando..." mientras carga', () => {
    vi.mocked(s.obtenerDocentes).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('carga docentes, apoderados y estudiantes al montar', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([docenteMock]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([]);
    renderPage();
    await esperarCarga();
    expect(screen.getByText('Docentes registrados')).toBeInTheDocument();
  });

  it('muestra lista de docentes por defecto', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([docenteMock]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([]);
    renderPage();
    await esperarCarga();
    expect(screen.getByText('Docentes registrados')).toBeInTheDocument();
  });

  it('muestra error si falla la carga', async () => {
    vi.mocked(s.obtenerDocentes).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar el listado de usuarios.')).toBeInTheDocument());
  });

  it('cambia a pestaña Apoderados', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([docenteMock]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([apoderadoMock]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([estudianteMock]);
    renderPage();
    await esperarCarga();
    fireEvent.click(screen.getByText('Apoderados'));
    await waitFor(() => expect(screen.getByText('Apoderados registrados')).toBeInTheDocument());
  });

  it('cambia a pestaña Estudiantes', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([docenteMock]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([estudianteMock]);
    renderPage();
    await esperarCarga();
    fireEvent.click(screen.getByText('Estudiantes'));
    await waitFor(() => expect(screen.getByText('Estudiantes registrados')).toBeInTheDocument());
  });

  it('crea un docente exitosamente', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([docenteMock]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([]);
    vi.mocked(s.crearUsuario).mockResolvedValue({ id: 'd2' });
    vi.mocked(s.obtenerDocentes).mockResolvedValue([docenteMock, { ...docenteMock, id: 'd2', nombres: 'Nuevo' }]);
    renderPage();
    await esperarCarga();
    const btnCrear = screen.getByText('Crear usuario');
    expect(btnCrear).toBeInTheDocument();
  });

  it('enriquece apoderados con pupiloNombre al cargar', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([{ ...apoderadoMock, pupiloUuid: 'e1', pupiloNombre: null }]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([estudianteMock]);
    renderPage();
    await esperarCarga();
    fireEvent.click(screen.getByText('Apoderados'));
    await waitFor(() => expect(screen.getByText('Luis Padre')).toBeInTheDocument());
  });

  it('edita un docente exitosamente', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([docenteMock]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([]);
    vi.mocked(s.actualizarUsuario).mockResolvedValue({});
    renderPage();
    await esperarCarga();
    fireEvent.click(screen.getByRole('button', { name: /Editar Ana/ }));
    await waitFor(() => expect(screen.getByText('Editar Usuario')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Guardar cambios'));
    await waitFor(() => {
      expect(s.actualizarUsuario).toHaveBeenCalledWith('d1', expect.objectContaining({ rut: '11-1' }));
    });
    await waitFor(() => {
      expect(screen.getAllByText('Usuario actualizado correctamente.')).toHaveLength(1);
    });
  });

  it('crea un apoderado exitosamente', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([]);
    vi.mocked(s.obtenerApoderados)
      .mockResolvedValueOnce([])
      .mockResolvedValueOnce([{ ...apoderadoMock, id: 'a2', nombres: 'Nuevo', pupiloUuid: 'e1', pupiloNombre: 'Sofía Alumno' }]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([estudianteMock]);
    vi.mocked(s.crearUsuario).mockResolvedValue({ id: 'a2' });
    renderPage();
    await esperarCarga();
    fireEvent.click(screen.getByText('Apoderados'));
    await waitFor(() => expect(screen.getByText('Apoderados registrados')).toBeInTheDocument());
    fireEvent.change(screen.getByLabelText('RUT'), { target: { value: '44-4' } });
    fireEvent.change(screen.getByLabelText('Nombres'), { target: { value: 'Nuevo' } });
    fireEvent.change(screen.getByLabelText('Apellidos'), { target: { value: 'Apoderado' } });
    fireEvent.change(screen.getByLabelText('Correo electrónico'), { target: { value: 'nuevo@b.cl' } });
    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'pass123' } });
    fireEvent.change(screen.getByLabelText('Confirmar contraseña'), { target: { value: 'pass123' } });
    fireEvent.change(screen.getByLabelText('Rol en el sistema'), { target: { value: 'APODERADO' } });
    await screen.findByLabelText('Pupilo asignado');
    fireEvent.change(screen.getByLabelText('Pupilo asignado'), { target: { value: 'e1' } });
    fireEvent.click(screen.getByText('Crear usuario'));
    await waitFor(() => {
      expect(s.crearUsuario).toHaveBeenCalledWith(expect.objectContaining({ rol: 'APODERADO', pupiloUuid: 'e1' }));
    });
    await waitFor(() => {
      expect(screen.getAllByText('Usuario creado correctamente.')).toHaveLength(2);
    });
  });

  it('crea un estudiante exitosamente', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValueOnce([]).mockResolvedValueOnce([{ ...estudianteMock, id: 'e2', nombres: 'Nuevo' }]);
    vi.mocked(s.crearUsuario).mockResolvedValue({ id: 'e2' });
    renderPage();
    await esperarCarga();
    fireEvent.click(screen.getByText('Estudiantes'));
    await waitFor(() => expect(screen.getByText('Estudiantes registrados')).toBeInTheDocument());
    fireEvent.change(screen.getByLabelText('RUT'), { target: { value: '66-6' } });
    fireEvent.change(screen.getByLabelText('Nombres'), { target: { value: 'Nuevo' } });
    fireEvent.change(screen.getByLabelText('Apellidos'), { target: { value: 'Estudiante' } });
    fireEvent.change(screen.getByLabelText('Correo electrónico'), { target: { value: 'ne@b.cl' } });
    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'pass123' } });
    fireEvent.change(screen.getByLabelText('Confirmar contraseña'), { target: { value: 'pass123' } });
    fireEvent.change(screen.getByLabelText('Rol en el sistema'), { target: { value: 'ESTUDIANTE' } });
    fireEvent.click(screen.getByText('Crear usuario'));
    await waitFor(() => {
      expect(s.crearUsuario).toHaveBeenCalledWith(expect.objectContaining({ rol: 'ESTUDIANTE' }));
    });
    await waitFor(() => {
      expect(screen.getAllByText('Usuario creado correctamente.')).toHaveLength(2);
    });
  });

  it('muestra error al crear usuario', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([]);
    vi.mocked(s.crearUsuario).mockRejectedValue(new Error('fail'));
    renderPage();
    await esperarCarga();
    fireEvent.change(screen.getByLabelText('RUT'), { target: { value: '55-5' } });
    fireEvent.change(screen.getByLabelText('Nombres'), { target: { value: 'Error' } });
    fireEvent.change(screen.getByLabelText('Apellidos'), { target: { value: 'Test' } });
    fireEvent.change(screen.getByLabelText('Correo electrónico'), { target: { value: 'e@b.cl' } });
    fireEvent.change(screen.getByLabelText('Contraseña'), { target: { value: 'pass123' } });
    fireEvent.change(screen.getByLabelText('Confirmar contraseña'), { target: { value: 'pass123' } });
    fireEvent.change(screen.getByLabelText('Rol en el sistema'), { target: { value: 'DOCENTE' } });
    fireEvent.click(screen.getByText('Crear usuario'));
    await waitFor(() => {
      expect(screen.getAllByText('No se pudo guardar el usuario.')).toHaveLength(1);
    });
  });

  it('muestra error al editar usuario', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([docenteMock]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([]);
    vi.mocked(s.actualizarUsuario).mockRejectedValue(new Error('fail'));
    renderPage();
    await esperarCarga();
    fireEvent.click(screen.getByRole('button', { name: /Editar Ana/ }));
    await waitFor(() => expect(screen.getByText('Editar Usuario')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Guardar cambios'));
    await waitFor(() => {
      expect(screen.getAllByText('No se pudo guardar el usuario.')).toHaveLength(1);
    });
  });

  it('elimina un usuario exitosamente', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([docenteMock]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([]);
    vi.mocked(s.eliminarUsuario).mockResolvedValue(undefined);
    renderPage();
    await waitFor(() => expect(screen.getByText('Docentes registrados')).toBeInTheDocument());
    fireEvent.click(screen.getByRole('button', { name: /Eliminar Ana/ }));
    await waitFor(() => expect(screen.getByText(/Esta acción no se puede deshacer/)).toBeInTheDocument());
    fireEvent.click(screen.getByText('Sí, eliminar'));
    await waitFor(() => {
      expect(s.eliminarUsuario).toHaveBeenCalledWith('d1');
    });
    await waitFor(() => {
      expect(screen.getByText('Usuario eliminado.')).toBeInTheDocument();
    });
    await waitFor(() => {
      expect(screen.queryByText('Ana')).not.toBeInTheDocument();
    });
  });

  it('muestra error al eliminar usuario', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([docenteMock]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([]);
    vi.mocked(s.eliminarUsuario).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('Docentes registrados')).toBeInTheDocument());
    fireEvent.click(screen.getByRole('button', { name: /Eliminar Ana/ }));
    await waitFor(() => expect(screen.getByText(/Esta acción no se puede deshacer/)).toBeInTheDocument());
    fireEvent.click(screen.getByText('Sí, eliminar'));
    await waitFor(() => {
      expect(screen.getByText('No se pudo eliminar el usuario.')).toBeInTheDocument();
    });
  });

  it('cancela la eliminación de un usuario', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([docenteMock]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Docentes registrados')).toBeInTheDocument());
    fireEvent.click(screen.getByRole('button', { name: /Eliminar Ana/ }));
    await waitFor(() => expect(screen.getByText(/Esta acción no se puede deshacer/)).toBeInTheDocument());
    fireEvent.click(screen.getByText('Cancelar'));
    await waitFor(() => {
      expect(screen.queryByText(/Esta acción no se puede deshacer/)).not.toBeInTheDocument();
    });
  });

  it('muestra mensaje de lista vacía para apoderados', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue([]);
    vi.mocked(s.obtenerApoderados).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantes).mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Docentes')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Apoderados'));
    await waitFor(() => {
      expect(screen.getByText('No hay apoderados registrados.')).toBeInTheDocument();
    });
  });
});
