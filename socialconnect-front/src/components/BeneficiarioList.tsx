import { useEffect, useState } from 'react';
import {
  Alert,
  Box,
  IconButton,
  Pagination,
  Paper,
  Skeleton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TableSortLabel,
  Typography,
} from '@mui/material';
import { Delete, Edit, PersonOff } from '@mui/icons-material';
import api from '../services/api';
import type { Beneficiario, Page } from '../types/beneficiario';

interface BeneficiarioListProps {
  refreshTrigger: number;
}

type OrderField = 'nome' | 'cpf';
type OrderDirection = 'asc' | 'desc';

export function BeneficiarioList({ refreshTrigger }: BeneficiarioListProps) {
  const [beneficiarios, setBeneficiarios] = useState<Beneficiario[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [orderBy, setOrderBy] = useState<OrderField>('nome');
  const [order, setOrder] = useState<OrderDirection>('asc');
  const [localRefresh, setLocalRefresh] = useState(0);

  useEffect(() => {
    setLoading(true);
    setError(null);
    api
      .get<Page<Beneficiario>>(
        `/beneficiarios?page=${page - 1}&size=10&sort=${orderBy},${order}`,
      )
      .then((response) => {
        setBeneficiarios(response.data.content);
        setTotalPages(Math.max(1, response.data.totalPages));
        setLoading(false);
      })
      .catch((err) => {
        console.error('Erro ao buscar beneficiários:', err);
        setError('Não foi possível carregar os beneficiários.');
        setLoading(false);
      });
  }, [refreshTrigger, localRefresh, page, orderBy, order]);

  const handleSort = (field: OrderField) => {
    const isAsc = orderBy === field && order === 'asc';
    setOrder(isAsc ? 'desc' : 'asc');
    setOrderBy(field);
    setPage(1);
  };

  const handleDelete = async (id: number, nome: string) => {
    try {
      await api.delete(`/beneficiarios/${id}`);
      setLocalRefresh((prev) => prev + 1);
    } catch (err) {
      console.error(`Erro ao excluir beneficiário ${nome}:`, err);
      setError(`Não foi possível excluir ${nome}.`);
    }
  };

  if (loading) {
    return (
      <Paper elevation={2} sx={{ p: 3 }} aria-busy="true" aria-label="Carregando beneficiários">
        <Skeleton variant="text" width="40%" height={32} sx={{ mb: 2 }} />
        <Skeleton variant="rectangular" height={48} sx={{ mb: 1 }} />
        <Skeleton variant="rectangular" height={48} sx={{ mb: 1 }} />
        <Skeleton variant="rectangular" height={48} />
      </Paper>
    );
  }

  return (
    <Paper elevation={2} sx={{ p: 2 }}>
      <Typography variant="h6" sx={{ mb: 2, px: 1 }}>
        Beneficiários Cadastrados
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      {beneficiarios.length === 0 ? (
        <Stack spacing={1} sx={{ py: 4, alignItems: 'center' }} role="status">
          <PersonOff fontSize="large" color="disabled" aria-hidden="true" />
          <Typography color="text.secondary">
            Nenhum beneficiário encontrado.
          </Typography>
        </Stack>
      ) : (
        <>
          <TableContainer sx={{ overflowX: 'auto' }}>
            <Table aria-label="Tabela de beneficiários">
              <TableHead>
                <TableRow>
                  <TableCell sortDirection={orderBy === 'nome' ? order : false}>
                    <TableSortLabel
                      active={orderBy === 'nome'}
                      direction={orderBy === 'nome' ? order : 'asc'}
                      onClick={() => handleSort('nome')}
                    >
                      Nome
                    </TableSortLabel>
                  </TableCell>
                  <TableCell sortDirection={orderBy === 'cpf' ? order : false}>
                    <TableSortLabel
                      active={orderBy === 'cpf'}
                      direction={orderBy === 'cpf' ? order : 'asc'}
                      onClick={() => handleSort('cpf')}
                    >
                      CPF
                    </TableSortLabel>
                  </TableCell>
                  <TableCell>Telefone</TableCell>
                  <TableCell>Endereço</TableCell>
                  <TableCell align="right">Ações</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {beneficiarios.map((b) => (
                  <TableRow key={b.idBeneficiario} hover>
                    <TableCell>{b.nome}</TableCell>
                    <TableCell>{b.cpf}</TableCell>
                    <TableCell>{b.telefone || '—'}</TableCell>
                    <TableCell>{b.endereco || '—'}</TableCell>
                    <TableCell align="right">
                      <IconButton
                        aria-label={`Editar beneficiário ${b.nome}`}
                        color="primary"
                        size="small"
                      >
                        <Edit fontSize="small" />
                      </IconButton>
                      <IconButton
                        aria-label={`Excluir beneficiário ${b.nome}`}
                        color="error"
                        size="small"
                        onClick={() => handleDelete(b.idBeneficiario, b.nome)}
                      >
                        <Delete fontSize="small" />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2 }}>
            <Pagination
              count={totalPages}
              page={page}
              onChange={(_, value) => setPage(value)}
              color="primary"
              aria-label="Paginação da lista de beneficiários"
            />
          </Box>
        </>
      )}
    </Paper>
  );
}
