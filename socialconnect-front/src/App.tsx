import { useState } from 'react';
import { Container, Stack, Typography } from '@mui/material';
import { BeneficiarioForm } from './components/BeneficiarioForm';
import { BeneficiarioList } from './components/BeneficiarioList';

function App() {
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Stack spacing={4}>
        <Typography variant="h4" sx={{ fontWeight: 700 }}>
          SocialConnect — Gestão de Beneficiários
        </Typography>
        <BeneficiarioForm onSuccess={() => setRefreshTrigger((prev) => prev + 1)} />
        <BeneficiarioList refreshTrigger={refreshTrigger} />
      </Stack>
    </Container>
  );
}

export default App;
