import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import LoginPage from '../../../features/auth/pages/LoginPage';
import { AuthProvider } from '../../../core/context/AuthContext';

describe('LoginPage', () => {
  it('renderiza AuthLayout con LoginForm', () => {
    render(<MemoryRouter><AuthProvider><LoginPage /></AuthProvider></MemoryRouter>);
    expect(screen.getByLabelText('Información institucional')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('12345678-9')).toBeInTheDocument();
  });
});
