import { useState } from 'react';
import { BeneficiarioForm } from './components/BeneficiarioForm';
import { BeneficiarioList } from './components/BeneficiarioList';

function App() {
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  return (
    <main>
      <h1>SocialConnect - Beneficiários</h1>
      <BeneficiarioForm onSuccess={() => setRefreshTrigger((prev) => prev + 1)} />
      <hr />
      <BeneficiarioList refreshTrigger={refreshTrigger} />
    </main>
  );
}

export default App;
