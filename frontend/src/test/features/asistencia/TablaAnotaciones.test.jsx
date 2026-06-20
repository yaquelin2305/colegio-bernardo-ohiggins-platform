import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import TablaAnotaciones from '../../../features/asistencia/components/TablaAnotaciones';

const alumnoBase = { id: 1, rut: '11-1', nombre: 'Juan' };
const alumno2 = { id: 2, rut: '22-2', nombre: 'Ana' };

function renderizar(props = {}) {
  return render(
    <TablaAnotaciones
      alumnos={[]}
      anotacionesPorAlumno={{}}
      panelActivo={null}
      formulario={{ tipo: 'positiva', descripcion: '' }}
      onTogglePanel={vi.fn()}
      onTipoChange={vi.fn()}
      onDescripcionChange={vi.fn()}
      onGuardar={vi.fn()}
      onCancelar={vi.fn()}
      guardadoDeshabilitado={false}
      {...props}
    />
  );
}

describe('TablaAnotaciones', () => {
  it('renderiza encabezados de tabla', () => {
    renderizar();
    expect(screen.getByText('RUT')).toBeInTheDocument();
    expect(screen.getByText('Alumno')).toBeInTheDocument();
    expect(screen.getByText('Positivas')).toBeInTheDocument();
    expect(screen.getByText('Negativas')).toBeInTheDocument();
    expect(screen.getByText('Historial')).toBeInTheDocument();
    expect(screen.getByText('Acción')).toBeInTheDocument();
  });

  it('muestra "—" cuando no hay anotaciones', () => {
    renderizar({ alumnos: [alumnoBase], anotacionesPorAlumno: { 1: [] } });
    expect(screen.getByText('—')).toBeInTheDocument();
    expect(screen.getByText('Juan')).toBeInTheDocument();
    expect(screen.getByText('11-1')).toBeInTheDocument();
  });

  it('muestra contadores de positivas y negativas', () => {
    const anotaciones = [
      { id: 1, tipo: 'positiva', descripcion: 'Buen trabajo' },
      { id: 2, tipo: 'positiva', descripcion: 'Participación' },
      { id: 3, tipo: 'negativa', descripcion: 'Llegó tarde' },
    ];
    renderizar({ alumnos: [alumnoBase], anotacionesPorAlumno: { 1: anotaciones } });
    expect(screen.getByText('2')).toBeInTheDocument();
    expect(screen.getByText('1')).toBeInTheDocument();
    expect(screen.getByText('Ver')).toBeInTheDocument();
  });

  it('toggle historial expande y colapsa anotaciones', () => {
    const anotaciones = [
      { id: 1, tipo: 'positiva', descripcion: 'Buen trabajo' },
    ];
    renderizar({ alumnos: [alumnoBase], anotacionesPorAlumno: { 1: anotaciones } });
    const btnVer = screen.getByText('Ver');
    fireEvent.click(btnVer);
    expect(screen.getByText('Buen trabajo')).toBeInTheDocument();
    expect(screen.getByText('Ocultar')).toBeInTheDocument();
    fireEvent.click(screen.getByText('Ocultar'));
    expect(screen.queryByText('Buen trabajo')).not.toBeInTheDocument();
  });

  it('toggle historial desde contador de positivas', () => {
    const anotaciones = [
      { id: 1, tipo: 'positiva', descripcion: 'Excelente' },
    ];
    renderizar({ alumnos: [alumnoBase], anotacionesPorAlumno: { 1: anotaciones } });
    const positivoBtn = screen.getByTitle('Ver anotaciones positivas');
    fireEvent.click(positivoBtn);
    expect(screen.getByText('Excelente')).toBeInTheDocument();
  });

  it('abre panel al hacer click en Agregar', () => {
    const onTogglePanel = vi.fn();
    renderizar({ alumnos: [alumnoBase], anotacionesPorAlumno: { 1: [] }, onTogglePanel });
    fireEvent.click(screen.getByText('Agregar'));
    expect(onTogglePanel).toHaveBeenCalledWith(1);
  });

  it('muestra formulario cuando panelActivo coincide', () => {
    renderizar({
      alumnos: [alumnoBase],
      anotacionesPorAlumno: { 1: [] },
      panelActivo: 1,
    });
    expect(screen.getByText(/Nueva anotación para/)).toBeInTheDocument();
    expect(screen.getByText('Guardar anotación')).toBeInTheDocument();
    expect(screen.getByText('Cancelar')).toBeInTheDocument();
  });

  it('cambia tipo de anotación a negativa', () => {
    const onTipoChange = vi.fn();
    renderizar({
      alumnos: [alumnoBase],
      anotacionesPorAlumno: { 1: [] },
      panelActivo: 1,
      onTipoChange,
    });
    const negativaRadio = screen.getByDisplayValue('negativa');
    fireEvent.click(negativaRadio);
    expect(onTipoChange).toHaveBeenCalledWith('negativa');
  });

  it('llama onDescripcionChange al escribir en el input', () => {
    const onDescripcionChange = vi.fn();
    renderizar({
      alumnos: [alumnoBase],
      anotacionesPorAlumno: { 1: [] },
      panelActivo: 1,
      onDescripcionChange,
    });
    const input = screen.getByPlaceholderText('Ej: Participación destacada en clase...');
    fireEvent.change(input, { target: { value: 'test' } });
    expect(onDescripcionChange).toHaveBeenCalled();
  });

  it('llama onGuardar al hacer submit del formulario', () => {
    const onGuardar = vi.fn();
    renderizar({
      alumnos: [alumnoBase],
      anotacionesPorAlumno: { 1: [] },
      panelActivo: 1,
      onGuardar,
    });
    const form = screen.getByText('Guardar anotación').closest('form');
    fireEvent.submit(form);
    expect(onGuardar).toHaveBeenCalled();
  });

  it('llama onCancelar al hacer click en Cancelar', () => {
    const onCancelar = vi.fn();
    renderizar({
      alumnos: [alumnoBase],
      anotacionesPorAlumno: { 1: [] },
      panelActivo: 1,
      onCancelar,
    });
    fireEvent.click(screen.getByText('Cancelar'));
    expect(onCancelar).toHaveBeenCalled();
  });

  it('deshabilita botón guardar cuando guardadoDeshabilitado es true', () => {
    renderizar({
      alumnos: [alumnoBase],
      anotacionesPorAlumno: { 1: [] },
      panelActivo: 1,
      guardadoDeshabilitado: true,
    });
    expect(screen.getByText('Guardar anotación')).toBeDisabled();
  });

  it('muestra historial con fecha formateada', () => {
    const anotaciones = [
      { id: 1, tipo: 'positiva', descripcion: 'Bien', fecha: '2026-06-15' },
    ];
    renderizar({ alumnos: [alumnoBase], anotacionesPorAlumno: { 1: anotaciones } });
    fireEvent.click(screen.getByText('Ver'));
    expect(screen.getByText('Bien')).toBeInTheDocument();
  });

  it('renderiza múltiples alumnos', () => {
    renderizar({
      alumnos: [alumnoBase, alumno2],
      anotacionesPorAlumno: { 1: [], 2: [] },
    });
    expect(screen.getByText('Juan')).toBeInTheDocument();
    expect(screen.getByText('Ana')).toBeInTheDocument();
  });
});
