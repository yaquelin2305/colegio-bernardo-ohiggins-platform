import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import FormularioMensaje from '../../../features/comunicaciones/components/FormularioMensaje';

function setup(overrides = {}) {
  const formulario = { destinatario: '', asunto: '', tipo: 'ADMINISTRATIVO', mensaje: '', canal: 'EMAIL', ...overrides.formulario };
  const onChange = overrides.onChange ?? vi.fn();
  const onEnviar = overrides.onEnviar ?? vi.fn();
  const props = { formulario, onChange, onEnviar, isLoading: overrides.isLoading ?? false, destinatarios: overrides.destinatarios ?? [] };
  return { ...render(<FormularioMensaje {...props} />), onChange, onEnviar, formulario };
}

describe('FormularioMensaje', () => {
  it('renderiza todos los campos del formulario', () => {
    setup({ destinatarios: [{ id: 'd1', nombre: 'Juan Pérez' }] });
    expect(screen.getByLabelText('Destinatario')).toBeInTheDocument();
    expect(screen.getByLabelText('Asunto')).toBeInTheDocument();
    expect(screen.getByLabelText('Tipo de Mensaje')).toBeInTheDocument();
    expect(screen.getByLabelText('Mensaje')).toBeInTheDocument();
    expect(screen.getByText('Correo Electrónico')).toBeInTheDocument();
    expect(screen.getByText('SMS')).toBeInTheDocument();
    expect(screen.getByText('WhatsApp')).toBeInTheDocument();
  });

  it('renderiza opciones de destinatarios', () => {
    setup({ destinatarios: [{ id: 'd1', nombre: 'Juan' }, { id: 'd2', nombre: 'María' }] });
    const select = screen.getByLabelText('Destinatario');
    expect(select).toContainElement(screen.getByText('Juan'));
    expect(select).toContainElement(screen.getByText('María'));
  });

  it('llama onChange al cambiar un campo', () => {
    const onChange = vi.fn();
    setup({ onChange, destinatarios: [{ id: 'd1', nombre: 'Juan' }] });
    fireEvent.change(screen.getByLabelText('Asunto'), { target: { value: 'Hola', name: 'asunto' } });
    expect(onChange).toHaveBeenCalled();
  });

  it('llama onEnviar al hacer submit', () => {
    const onEnviar = vi.fn(e => e.preventDefault());
    setup({
      onEnviar,
      formulario: { destinatario: 'd1', asunto: 'Test', tipo: 'CONSULTA', mensaje: 'Cuerpo', canal: 'EMAIL' },
      destinatarios: [{ id: 'd1', nombre: 'Juan' }],
    });
    fireEvent.submit(screen.getByRole('button', { name: /enviar mensaje/i }));
    expect(onEnviar).toHaveBeenCalled();
  });

  it('muestra "Enviando..." y deshabilita botón cuando isLoading', () => {
    setup({ isLoading: true });
    expect(screen.getByRole('button', { name: /enviando/i })).toBeDisabled();
  });

  it('marca el canal activo visualmente', () => {
    setup({ formulario: { destinatario: '', asunto: '', tipo: 'ADMINISTRATIVO', mensaje: '', canal: 'SMS' } });
    expect(screen.getByText('SMS').closest('label')).toHaveClass('redactar__canal-opcion--activo');
  });

  it('preselecciona el canal EMAIL por defecto', () => {
    setup();
    expect(screen.getByDisplayValue('EMAIL')).toBeChecked();
  });
});
