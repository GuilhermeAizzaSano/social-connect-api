export interface Beneficiario {
  idBeneficiario: number;
  nome: string;
  cpf: string;
  telefone: string;
  endereco: string;
  situacaoVulnerabilidade: string;
  dataCadastro: string;
}

export interface BeneficiarioRequest {
  nome: string;
  cpf: string;
  telefone?: string;
  endereco?: string;
  situacaoVulnerabilidade?: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
