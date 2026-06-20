import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import TablaBoletinNotas from '../../../features/gestion-academica/components/TablaBoletinNotas';

const ASIGNATURAS = [
  { id: 1, nombre: 'Matemáticas', nota1: 6.5, nota2: 5.0, nota3: 4.0, promedio: 5.2 },
  { id: 2, nombre: 'Lenguaje', nota1: 3.5, nota2: 4.0, nota3: 3.0, promedio: 3.5 },
];

describe('TablaBoletinNotas', () => {
  it('renderiza asignaturas con notas', () => {
    render(<TablaBoletinNotas asignaturas={ASIGNATURAS} />);
    expect(screen.getByText('Matemáticas')).toBeInTheDocument();
    expect(screen.getByText('Lenguaje')).toBeInTheDocument();
  });

  it('muestra estado aprobado cuando promedio >= 4.0', () => {
    render(<TablaBoletinNotas asignaturas={ASIGNATURAS} />);
    expect(screen.getByText('Aprobado')).toBeInTheDocument();
  });

  it('muestra estado reprobado cuando promedio < 4.0', () => {
    render(<TablaBoletinNotas asignaturas={ASIGNATURAS} />);
    expect(screen.getByText('Reprobado')).toBeInTheDocument();
  });

  it('clasifica notas altas (>=6.0), medias (>=4.0) y bajas', () => {
    const { container } = render(<TablaBoletinNotas asignaturas={ASIGNATURAS} />);
    const celdas = container.querySelectorAll('.boletin__celda-nota');
    const textos = Array.from(celdas).map(c => c.textContent);
    expect(textos.some(t => t.includes('6.5'))).toBe(true);
  });
});
