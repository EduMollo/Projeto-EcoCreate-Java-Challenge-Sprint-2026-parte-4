const {test}=require('node:test');const assert=require('node:assert/strict');const vm=require('node:vm');const fs=require('node:fs');const codigo=fs.readFileSync(require('node:path').join(__dirname,'../src/main/resources/static/js/tema.js'),'utf8');
function ambiente(preferencia,escuro=false,bloqueado=false){
 const attrs={},eventos={},salvos={};let clique;const botao={setAttribute:(k,v)=>attrs[k]=v,addEventListener:(tipo,fn)=>clique=fn};
 const document={documentElement:{setAttribute:(k,v)=>attrs[k]=v},querySelectorAll:()=>[botao],addEventListener:(tipo,fn)=>eventos[tipo]=fn};
 vm.runInNewContext(codigo,{document,window:{matchMedia:()=>({matches:escuro})},localStorage:{getItem:()=>{if(bloqueado)throw Error();return preferencia},setItem:(k,v)=>{if(bloqueado)throw Error();salvos[k]=v}}});eventos.DOMContentLoaded();return {attrs,botao,salvos,clicar:()=>clique()};
}
test('respeita preferencia salva mesmo com sistema claro',()=>{const a=ambiente('dark');assert.equal(a.attrs['data-bs-theme'],'dark');assert.equal(a.botao.textContent,'Tema claro');assert.equal(a.attrs['aria-pressed'],'true')});
test('usa sistema quando preferencia e invalida',()=>{assert.equal(ambiente('invalido',true).attrs['data-bs-theme'],'dark')});
test('alterna nos dois sentidos e salva escolha',()=>{const a=ambiente('light');a.clicar();assert.equal(a.salvos['clyvovet-tema'],'dark');assert.equal(a.attrs['data-bs-theme'],'dark');a.clicar();assert.equal(a.salvos['clyvovet-tema'],'light');assert.equal(a.attrs['aria-label'],'Ativar tema escuro')});
test('armazenamento bloqueado nao impede a alternancia',()=>{const a=ambiente(null,false,true);a.clicar();assert.equal(a.attrs['data-bs-theme'],'dark')});
