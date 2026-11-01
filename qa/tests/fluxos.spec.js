const {test,expect}=require('@playwright/test');
async function entrar(page,usuario,senha){await page.goto('/login');await page.locator('#username').fill(usuario);await page.locator('#password').fill(senha);await page.getByRole('button',{name:'Entrar',exact:true}).click();}
test('QA01 - tutor autenticado consulta seus pets',async({page})=>{
 await entrar(page,'tutor','tutor123');await page.goto('/meus-pets');
 await expect(page.getByRole('heading',{name:'Meus pets',exact:true})).toBeVisible();
 await expect(page.getByRole('heading',{name:'Thor',exact:true})).toBeVisible();
 await expect(page.getByRole('heading',{name:'Luna',exact:true})).toBeVisible();
 await expect(page.getByRole('heading',{name:'Rex',exact:true})).toHaveCount(0);
});
test('QA02 - senha incorreta impede autenticacao',async({page})=>{
 await entrar(page,'tutor','senha-incorreta');await expect(page).toHaveURL(/falha=true/);
 await page.goto('/meus-pets');await expect(page).toHaveURL(/\/login$/);
});
test('QA03 - alterar ID nao permite acesso ao pet de outro tutor',async({page})=>{
 await entrar(page,'tutor','tutor123');await page.goto('/pets/3/dashboard');
 await expect(page.getByRole('heading',{name:'Acesso negado',exact:true})).toBeVisible();
 await expect(page.locator('.score-risco')).toHaveCount(0);
});
test('QA04 - agendar, iniciar e concluir atendimento',async({page})=>{
 await entrar(page,'vet','vet123');await page.goto('/consultas/nova');
 await page.locator('#idPet').selectOption('1');await page.locator('#idClinica').selectOption('1');
 const hoje=new Date();const data=hoje.getFullYear()+'-'+String(hoje.getMonth()+1).padStart(2,'0')+'-'+String(hoje.getDate()).padStart(2,'0');
 await page.locator('#dataConsulta').fill(data);await page.locator('#tipoConsulta').selectOption('ROTINA');
 await page.locator('#veterinario').fill('Veterinario QA');await page.getByRole('button',{name:'Agendar',exact:true}).click();
 await expect(page).toHaveURL(/\/consultas\/\d+$/);
 await page.getByRole('button',{name:'Iniciar atendimento',exact:true}).click();
 await page.getByRole('link',{name:'Registrar atendimento',exact:true}).click();
 await page.locator('#diagnostico').fill('Registro de teste automatizado');await page.locator('#valor').fill('180.00');
 await page.getByRole('button',{name:'Concluir atendimento',exact:true}).click();
 await expect(page.getByRole('button',{name:'Iniciar atendimento',exact:true})).toHaveCount(0);
 await expect(page.locator('section')).toContainText('Registro de teste automatizado');
});
test('QA05 - tema escuro persiste entre paginas e recarregamento',async({page})=>{
 await page.goto('/login');await page.evaluate(()=>localStorage.setItem('clyvovet-tema','light'));await page.reload();
 await page.locator('[data-alternar-tema]').click();await expect(page.locator('html')).toHaveAttribute('data-bs-theme','dark');
 await page.goto('/cadastro');await page.reload();await expect(page.locator('html')).toHaveAttribute('data-bs-theme','dark');
 await page.locator('[data-alternar-tema]').click();await expect(page.locator('html')).toHaveAttribute('data-bs-theme','light');
});
test('QA06 - tutor nao acessa relatorios administrativos',async({page})=>{
 await entrar(page,'tutor','tutor123');await page.goto('/relatorios');
 await expect(page.getByRole('heading',{name:'Acesso negado',exact:true})).toBeVisible();
});
