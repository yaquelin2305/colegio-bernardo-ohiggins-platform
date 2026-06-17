import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import ListaMensajes from '../../../features/comunicaciones/components/ListaMensajes';

const mensajesBase = [
  { id: 1, remitente: 'Docente Juan', asunto: 'Reunión mañana', leido: false, fecha: '2024-06-01T10:00:00Z', tipo: 'AVISO' },
  { id: 2, remitente: 'Admin',        asunto: 'Circular mensual', leido: true, fecha: '2024-06-02T10:00:00Z', tipo: null },
];

describe('ListaMensajes', () => {
  it('muestra mensaje vacío cuando no hay mensajes', () => {
    render(<ListaMensajes mensajes={[]} onSeleccionarMensaje={() => {}} />);
    expect(screen.getByText('No hay mensajes en tu bandeja.')).toBeInTheDocument();
  });

  it('renderiza todos los mensajes de la lista', () => {
    render(<ListaMensajes mensajes={mensajesBase} onSeleccionarMensaje={() => {}} />);
    expect(screen.getByText('Docente Juan')).toBeInTheDocument();
    expect(screen.getByText('Reunión mañana')).toBeInTheDocument();
    expect(screen.getByText('Admin')).toBeInTheDocument();
    expect(screen.getByText('Circular mensual')).toBeInTheDocument();
  });

  it('llama a onSeleccionarMensaje con el id correcto al hacer click', () => {
    const onSeleccionar = vi.fn();
    render(<ListaMensajes mensajes={mensajesBase} onSeleccionarMensaje={onSeleccionar} />);
    fireEvent.click(screen.getAllByRole('listitem')[0]);
    expect(onSeleccionar).toHaveBeenCalledWith(1);
  });

  it('aplica clase no-leido a mensajes no leídos', () => {
    const { container } = render(<ListaMensajes mensajes={mensajesBase} onSeleccionarMensaje={() => {}} />);
    expect(container.querySelector('.bandeja__tarjeta--no-leido')).toBeInTheDocument();
  });

  it('aplica clase leido a mensajes leídos', () => {
    const { container } = render(<ListaMensajes mensajes={mensajesBase} onSeleccionarMensaje={() => {}} />);
    expect(container.querySelector('.bandeja__tarjeta--leido')).toBeInTheDocument();
  });

  it('muestra el tipo del mensaje cuando está presente', () => {
    render(<ListaMensajes mensajes={mensajesBase} onSeleccionarMensaje={() => {}} />);
    expect(screen.getByText('AVISO')).toBeInTheDocument();
  });

  it('no muestra etiqueta de tipo cuando el campo es null', () => {
    const solo = [{ id: 2, remitente: 'Admin', asunto: 'Sin tipo', leido: true, fecha: null, tipo: null }];
    const { container } = render(<ListaMensajes mensajes={solo} onSeleccionarMensaje={() => {}} />);
    expect(container.querySelector('.bandeja__etiqueta')).not.toBeInTheDocument();
  });

  it('usa ul con role list como contenedor', () => {
    render(<ListaMensajes mensajes={mensajesBase} onSeleccionarMensaje={() => {}} />);
    expect(screen.getByRole('list')).toBeInTheDocument();
  });
});
