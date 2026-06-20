import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import TabsUsuarios from '../../../features/usuarios/components/TabsUsuarios';

describe('TabsUsuarios', () => {
  it('renderiza los 3 tabs', () => {
    render(<TabsUsuarios tabActiva="docentes" onCambiarTab={vi.fn()} />);
    expect(screen.getByText('Docentes')).toBeInTheDocument();
    expect(screen.getByText('Apoderados')).toBeInTheDocument();
    expect(screen.getByText('Estudiantes')).toBeInTheDocument();
  });

  it('marca el tab activo', () => {
    render(<TabsUsuarios tabActiva="apoderados" onCambiarTab={vi.fn()} />);
    expect(screen.getByText('Apoderados').closest('button')).toHaveClass('gestion-usuarios__tab--activa');
  });

  it('llama onCambiarTab con la key al hacer click', () => {
    const onCambiarTab = vi.fn();
    render(<TabsUsuarios tabActiva="docentes" onCambiarTab={onCambiarTab} />);
    fireEvent.click(screen.getByText('Estudiantes'));
    expect(onCambiarTab).toHaveBeenCalledWith('estudiantes');
  });
});
