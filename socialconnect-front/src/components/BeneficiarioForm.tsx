import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Paper,
  TextField,
  Typography,
} from '@mui/material';
import { beneficiarioSchema, type BeneficiarioFormData } from '../schemas/beneficiarioSchema';
import api from '../services/api';

interface BeneficiarioFormProps {
  onSuccess: () => void;
}

export function BeneficiarioForm({ onSuccess }: BeneficiarioFormProps) {
  const [feedback, setFeedback] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    reset,
  } = useForm<BeneficiarioFormData>({
    resolver: zodResolver(beneficiarioSchema),
  });

  const onSubmit = async (data: BeneficiarioFormData) => {
    setFeedback(null);
    try {
      await api.post('/beneficiarios', {
        nome: data.nome,
        cpf: data.cpf,
        telefone: data.telefone || undefined,
        endereco: data.endereco || undefined,
        situacaoVulnerabilidade: data.situacaoVulnerabilidade || undefined,
      });
      reset();
      setFeedback({ type: 'success', message: 'Beneficiário cadastrado com sucesso!' });
      onSuccess();
    } catch (error) {
      console.error('Erro ao criar beneficiário:', error);
      setFeedback({
        type: 'error',
        message: 'Erro ao cadastrar beneficiário. Verifique os dados e tente novamente.',
      });
    }
  };

  return (
    <Paper elevation={2} sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        Cadastrar Beneficiário
      </Typography>

      {feedback && (
        <Alert severity={feedback.type} sx={{ mb: 2 }} role="status">
          {feedback.message}
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit(onSubmit)} noValidate>
        <TextField
          label="Nome completo"
          {...register('nome')}
          error={!!errors.nome}
          helperText={errors.nome?.message}
          fullWidth
          margin="normal"
          required
        />

        <TextField
          label="CPF (somente números)"
          {...register('cpf')}
          error={!!errors.cpf}
          helperText={errors.cpf?.message}
          fullWidth
          margin="normal"
          required
          slotProps={{ htmlInput: { maxLength: 11 } }}
        />

        <TextField
          label="Telefone com DDD"
          {...register('telefone')}
          error={!!errors.telefone}
          helperText={errors.telefone?.message}
          fullWidth
          margin="normal"
        />

        <TextField
          label="Endereço"
          {...register('endereco')}
          error={!!errors.endereco}
          helperText={errors.endereco?.message}
          fullWidth
          margin="normal"
        />

        <TextField
          label="Situação de vulnerabilidade"
          {...register('situacaoVulnerabilidade')}
          error={!!errors.situacaoVulnerabilidade}
          helperText={errors.situacaoVulnerabilidade?.message}
          fullWidth
          margin="normal"
          multiline
          rows={2}
        />

        <Button
          type="submit"
          variant="contained"
          disabled={isSubmitting}
          startIcon={isSubmitting ? <CircularProgress size={20} color="inherit" /> : undefined}
          sx={{ mt: 2 }}
        >
          {isSubmitting ? 'Cadastrando...' : 'Cadastrar'}
        </Button>
      </Box>
    </Paper>
  );
}
