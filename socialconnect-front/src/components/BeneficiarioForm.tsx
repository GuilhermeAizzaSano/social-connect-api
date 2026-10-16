import { useState } from 'react';
import api from '../services/api';

interface BeneficiarioFormProps {
  onSuccess: () => void;
}

export function BeneficiarioForm({ onSuccess }: BeneficiarioFormProps) {
  const [nome, setNome] = useState('');
  const [cpf, setCpf] = useState('');
  const [telefone, setTelefone] = useState('');
  const [endereco, setEndereco] = useState('');
  const [situacaoVulnerabilidade, setSituacaoVulnerabilidade] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [feedback, setFeedback] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFeedback(null);

    if (!nome.trim() || !cpf.trim()) {
      setFeedback({ type: 'error', message: 'Nome e CPF são obrigatórios.' });
      return;
    }

    setSubmitting(true);
    try {
      await api.post('/beneficiarios', {
        nome: nome.trim(),
        cpf: cpf.trim(),
        telefone: telefone.trim() || undefined,
        endereco: endereco.trim() || undefined,
        situacaoVulnerabilidade: situacaoVulnerabilidade.trim() || undefined,
      });
      setNome('');
      setCpf('');
      setTelefone('');
      setEndereco('');
      setSituacaoVulnerabilidade('');
      setFeedback({ type: 'success', message: 'Beneficiário cadastrado com sucesso!' });
      onSuccess();
    } catch (error) {
      console.error('Erro ao criar beneficiário:', error);
      setFeedback({ type: 'error', message: 'Erro ao cadastrar beneficiário.' });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Cadastrar Beneficiário</h2>
      {feedback && <p role="status">{feedback.message}</p>}
      <div>
        <input
          value={nome}
          onChange={(e) => setNome(e.target.value)}
          placeholder="Nome completo"
          required
        />
      </div>
      <div>
        <input
          value={cpf}
          onChange={(e) => setCpf(e.target.value)}
          placeholder="CPF (11 dígitos)"
          required
        />
      </div>
      <div>
        <input
          value={telefone}
          onChange={(e) => setTelefone(e.target.value)}
          placeholder="Telefone"
        />
      </div>
      <div>
        <input
          value={endereco}
          onChange={(e) => setEndereco(e.target.value)}
          placeholder="Endereço"
        />
      </div>
      <div>
        <input
          value={situacaoVulnerabilidade}
          onChange={(e) => setSituacaoVulnerabilidade(e.target.value)}
          placeholder="Situação de vulnerabilidade"
        />
      </div>
      <button type="submit" disabled={submitting}>
        {submitting ? 'Cadastrando...' : 'Cadastrar'}
      </button>
    </form>
  );
}
