import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import PanelMatricula from '../../../features/gestion-academica/components/PanelMatricula';

describe('PanelMatricula', () => {
  const CURSO = { id: 1, nombre: '1°A' };
  const DISPONIBLES = [{ id: 'a1', nombre: 'Carlos', apellido: 'González', rut: '11111111-1' }];

  it('renderiza lista de alumnos disponibles', () => {
    render(<PanelMatricula curso={CURSO} disponibles={DISPONIBLES} alumnoSeleccionadoId="" onAlumnoChange={vi.fn()} onMatricular={vi.fn()} onCancelar={vi.fn()} />);
    expect(screen.getByText('Carlos González (11111111-1)')).toBeInTheDocument();
    expect(screen.getByText('Matricular alumno en 1°A')).toBeInTheDocument();
  });

  it('llama onMatricular al enviar el formulario', () => {
    const onMatricular = vi.fn();
    render(<PanelMatricula curso={CURSO} disponibles={DISPONIBLES} alumnoSeleccionadoId="a1" onAlumnoChange={vi.fn()} onMatricular={onMatricular} onCancelar={vi.fn()} />);
    fireEvent.click(screen.getByText('Guardar matrícula'));
    expect(onMatricular).toHaveBeenCalled();
  });
});
