(() => {
 const atual = location.pathname.replace(/\/$/, '') || '/';
 document.querySelectorAll('.navbar-nav .nav-link').forEach(link => {
  const destino = new URL(link.href, location.href).pathname.replace(/\/$/, '') || '/';
  if (atual === destino || (destino !== '/' && atual.startsWith(destino + '/')) || (destino === '/' && atual === '/home')) {
   link.classList.add('active'); link.setAttribute('aria-current','page');
  }
 });
})();