(() => {
 const chave = 'clyvovet-tema';
 let tema;
 try { tema = localStorage.getItem(chave); } catch (_) { /* armazenamento indisponível */ }
 if (!['light', 'dark'].includes(tema)) tema = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
 function aplicar() {
  document.documentElement.setAttribute('data-bs-theme', tema);
  document.querySelectorAll('[data-alternar-tema]').forEach(botao => {
   botao.textContent = tema === 'dark' ? 'Tema claro' : 'Tema escuro';
   botao.setAttribute('aria-label', 'Ativar ' + botao.textContent.toLowerCase());
   botao.setAttribute('aria-pressed', String(tema === 'dark'));
  });
 }
 aplicar();
 document.addEventListener('DOMContentLoaded', () => {
  aplicar();
  document.querySelectorAll('[data-alternar-tema]').forEach(botao => botao.addEventListener('click', () => {
   tema = tema === 'dark' ? 'light' : 'dark'; aplicar();
   try { localStorage.setItem(chave, tema); } catch (_) { /* mantém preferência nesta página */ }
  }));
 });
})();