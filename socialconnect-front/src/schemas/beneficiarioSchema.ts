import { z } from 'zod';

export const beneficiarioSchema = z.object({
  nome: z
    .string()
    .trim()
    .min(3, 'Nome deve ter pelo menos 3 caracteres')
    .max(150, 'Nome deve ter no máximo 150 caracteres'),
  cpf: z
    .string()
    .trim()
    .regex(/^\d{11}$/, 'CPF deve ter exatamente 11 dígitos numéricos'),
  telefone: z
    .string()
    .trim()
    .regex(/^\d{10,11}$/, 'Telefone deve ter 10 ou 11 dígitos')
    .or(z.literal(''))
    .optional(),
  endereco: z
    .string()
    .trim()
    .max(255, 'Endereço deve ter no máximo 255 caracteres')
    .optional(),
  situacaoVulnerabilidade: z
    .string()
    .trim()
    .max(500, 'Situação deve ter no máximo 500 caracteres')
    .optional(),
});

export type BeneficiarioFormData = z.infer<typeof beneficiarioSchema>;
