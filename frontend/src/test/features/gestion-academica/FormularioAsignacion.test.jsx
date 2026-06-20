import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import FormularioAsignacion from '../../../features/gestion-academica/components/FormularioAsignacion';

describe('FormularioAsignacion', () => {
  const DOCENTES = [{ id: 'd1', nombre: 'Profesor A' }];
  const CURSOS = [{ id: 1, nombre: '1°A' }];
  const ASIGNATURAS = [{ id: 10, nombre: 'Matemáticas' }];
  const FORMULARIO = { docenteId: '', cursoId: '', asignaturaId: '' };

  it('renderiza formulario con selects', () => {
    render(<FormularioAsignacion formulario={FORMULARIO} docentes={DOCENTES} cursos={CURSOS} asignaturas={ASIGNATURAS} onChange={vi.fn()} onSubmit={vi.fn()} />);
    expect(screen.getByText('Profesor A')).toBeInTheDocument();
    expect(screen.getByText('1°A')).toBeInTheDocument();
    expect(screen.getByText('Matemáticas')).toBeInTheDocument();
    expect(screen.getByText('Asignar')).toBeInTheDocument();
  });

  it('llama onSubmit al enviar el formulario', () => {
    const onSubmit = vi.fn();
    render(<FormularioAsignacion formulario={FORMULARIO} docentes={DOCENTES} cursos={CURSOS} asignaturas={ASIGNATURAS} onChange={vi.fn()} onSubmit={onSubmit} />);
    fireEvent.click(screen.getByText('Asignar'));
    expect(onSubmit).toHaveBeenCalled();
  });
});
