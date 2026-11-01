"""Consolida os relatorios reais locais, sem incluir credenciais ou telas."""
from pathlib import Path
from datetime import datetime, timezone, timedelta
import hashlib
import json
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
reports = sorted((ROOT / 'target/surefire-reports').glob('TEST-*.xml'))
assert reports, 'Execute primeiro a suite Maven.'
suites = []
for path in reports:
    suite = ET.parse(path).getroot()
    suites.append({'suite': suite.get('name'), **{
        key: int(suite.get(key, 0)) for key in ('tests', 'failures', 'errors', 'skipped')}})
totals = {key: sum(s[key] for s in suites) for key in ('tests', 'failures', 'errors', 'skipped')}
assert totals['failures'] == totals['errors'] == 0
pw = ET.parse(ROOT / 'qa/test-results/results.xml').getroot()
assert int(pw.get('tests', 0)) == 6
assert int(pw.get('failures', 0)) == int(pw.get('errors', 0)) == int(pw.get('skipped', 0)) == 0
jar = ROOT / 'target/clyvovet-web-4.0.0.jar'
assert jar.exists()
summary = {
    'verificado_em': datetime.now(timezone(timedelta(hours=-3))).isoformat(),
    'ambiente': 'Java 21; H2 isolado; Chromium Playwright 1.56.1',
    'junit': {**totals, 'executados': totals['tests'] - totals['skipped'], 'suites': suites},
    'playwright': {'testes': 6, 'aprovados': 6, 'falhas': 0, 'duracao_segundos': float(pw.get('time')),
                   'casos': [c.get('name') for c in pw.iter('testcase')]},
    'jar': {'arquivo': jar.name, 'sha256': hashlib.sha256(jar.read_bytes()).hexdigest()},
    'limites': [
        'H2 e contratos de tela/JDBC nao comprovam execucao real de PL/SQL.',
        'Os quatro testes opcionais de bancos externos nao foram ativados nesta suite.',
        'Oracle FIAP e MongoDB reais foram validados em 01/11/2026; evidencia na entrega Database.',
        'Video narrado e apresentacao da equipe permanecem a cargo dos integrantes.'
    ]
}
(ROOT / 'docs/VALIDACAO-TECNICA.json').write_text(json.dumps(summary, ensure_ascii=False, indent=2), encoding='utf-8')
# Evidencia de resultados sem anexos de navegador, caminhos privados ou saidas do terminal.
for suite in pw.iter('testsuite'):
    suite.attrib.pop('hostname', None)
for case in pw.iter('testcase'):
    for child in list(case):
        if child.tag in {'system-out', 'system-err'}:
            case.remove(child)
dest = ROOT / 'docs/evidencias'
dest.mkdir(exist_ok=True)
ET.ElementTree(pw).write(dest / 'playwright-junit.xml', encoding='utf-8', xml_declaration=True)
print(json.dumps({'junit': totals, 'playwright': summary['playwright'], 'jar': jar.name}, ensure_ascii=False, indent=2))
