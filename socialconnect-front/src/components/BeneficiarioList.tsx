import { useEffect, useState } from 'react';
import api from '../services/api';
import type { Beneficiario, Page } from '../types/beneficiario';

interface BeneficiarioListProps {
  refreshTrigger: number;
}

export function BeneficiarioList({ refreshTrigger }: BeneficiarioListProps) {
  const [beneficiarios, setBeneficiarios] = useState<Beneficiario[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
    api
      .get<Page<Beneficiario>>('/beneficiarios?page=0&size=10')
      .then((response) => {
        setBeneficiarios(response.data.content);
        setLoading(false);
      })
      .catch((err) => {
        console.error('Erro ao buscar beneficiários:', err);
        setError('Não foi possível carregar os beneficiários.');
        setLoading(false);
      });
  }, [refreshTrigger]);

  if (loading) return <p>Carregando...</p>;
  if (error) return <p>{error}</p>;

  return (
    <table>
      <thead>
        <tr>
          <th>Nome</th>
          <th>CPF</th>
          <th>Telefone</th>
          <th>Endereço</th>
        </tr>
      </thead>
      <tbody>
        {beneficiarios.map((b) => (
          <tr key={b.idBeneficiario}>
            <td>{b.nome}</td>
            <td>{b.cpf}</td>
            <td>{b.telefone}</td>
            <td>{b.endereco}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
