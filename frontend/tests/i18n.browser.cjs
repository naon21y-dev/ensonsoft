// Start Vite first. Run with PLAYWRIGHT_MODULE pointing to an available Playwright
// package, or install Playwright in your test environment. No real API is called.
const { chromium } = require(process.env.PLAYWRIGHT_MODULE || 'playwright');
const assert = require('node:assert/strict');

const original = 'DB 원문 보존';
const site = { id: 1, siteCode: 'SITE-001', name: original, address: original, description: original, siteType: 'TOLL_GATE', status: 'NORMAL', managerName: original, managerTel: '010-1234-5678', createdAt: '2026-09-29T09:00:00', updatedAt: '2026-09-29T09:00:00' };
const equipment = { id: 1, equipmentCode: 'EQ-001', name: original, siteId: 1, siteName: original, equipmentType: 'LPR_CAMERA', status: 'NORMAL', description: original, installedAt: '2026-09-29', createdAt: site.createdAt, updatedAt: site.updatedAt };
const board = { id: 1, title: original, content: original, writer: 'tester', viewCount: 1, createdAt: site.createdAt, updatedAt: site.updatedAt };
const maintenance = { id: 1, maintenanceCode: 'M-001', title: original, content: original, siteName: original, siteCode: 'SITE-001', equipmentName: original, equipmentCode: 'EQ-001', status: 'REPORTED', occurredAt: site.createdAt, reportedAt: site.createdAt, reportedBy: 'tester' };
const event = { id: 1, eventType: 'ACCIDENT', status: 'UNPROCESSED', severity: 'CRITICAL', location: original, cameraId: 'CAM-001', detectedAt: site.createdAt, occurredAt: site.createdAt, description: original };
const member = { id: 1, username: 'tester', name: original, email: 'tester@example.com', role: 'ADMIN', enabled: true };
const qna = { id: 1, title: original, content: original, answer: original, username: 'tester', category: 'GENERAL', status: 'ANSWERED', secret: true, createdAt: site.createdAt, answeredAt: site.createdAt, answerUsername: 'tester' };
const counts = keys => Object.fromEntries(keys.split(' ').map(key => [key, 1]));
const dashboard = { totalSites: 1, totalEquipments: 1, normalEquipments: 1, errorEquipments: 0, pendingMaintenances: 1, totalEvents: 1, generatedAt: site.createdAt, equipmentStatuses: counts('NORMAL WARNING ERROR MAINTENANCE OFFLINE INACTIVE'), siteStatuses: counts('NORMAL WARNING ERROR MAINTENANCE INACTIVE'), maintenanceStatuses: counts('REPORTED IN_PROGRESS COMPLETED'), eventStatuses: counts('UNPROCESSED PROCESSING COMPLETED'), recentMaintenances: [maintenance], recentEvents: [event] };

(async () => {
 const browser = await chromium.launch({ headless: true, channel: process.env.BROWSER_CHANNEL || 'msedge' });
 try {
  const context = await browser.newContext({ viewport: { width: 1440, height: 1000 } });
  await context.addInitScript(() => {
   localStorage.setItem('accessToken', 'test-token');
   localStorage.setItem('username', 'tester');
   localStorage.setItem('role', 'ADMIN');
  });
  let requests = [];
  let failBoards = false;
  let emptyLists = false;
  await context.route(url => url.pathname.startsWith('/api/'), async route => {
   const request = route.request();
   requests.push({ method: request.method(), url: request.url(), body: request.postData(), authorization: request.headers().authorization });
   const url = new URL(request.url()); const p = url.pathname.replace('/api', '');
   if (failBoards && p === '/boards') return route.fulfill({ status: 500, contentType: 'application/json', body: '{}' });
   let data;
   if (p === '/dashboard') data = dashboard;
   else if (p.endsWith('/histories')) data = [];
   else if (p === '/user/me') data = member;
   else if (p === '/admin/members') data = [member];
   else if (p === '/qna') data = emptyLists ? [] : [qna];
   else if (/^\/qna\/\d+$/.test(p)) data = qna;
   else if (/^\/boards\/\d+$/.test(p)) data = board;
   else if (p === '/boards') data = emptyLists ? [] : [board];
   else if (/^\/sites\/\d+$/.test(p)) data = site;
   else if (p.startsWith('/sites')) data = emptyLists ? [] : [site];
   else if (/^\/equipments\/\d+$/.test(p)) data = equipment;
   else if (p.startsWith('/equipments')) data = emptyLists ? [] : [equipment];
   else if (/^\/maintenance\/\d+$/.test(p)) data = maintenance;
   else if (p === '/maintenance') data = emptyLists ? [] : [maintenance];
   else if (/^\/monitoring\/events\/\d+$/.test(p)) data = event;
   else if (p.startsWith('/monitoring/events')) data = emptyLists ? [] : [event];
   else if (p.startsWith('/monitoring/vehicles')) data = emptyLists ? [] : [{ id: 1, plateNumber: '12가3456', location: original, status: 'NORMAL', recognizedAt: site.createdAt, confidence: 99 }];
   else throw new Error('Unexpected API request: ' + p);
   await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(data) });
  });
  const page = await context.newPage();
  const errors = [];
  page.on('pageerror', e => errors.push(e.message));
  page.on('console', msg => { if (msg.type() === 'warning' && /intlify|not defined|Unhandled/.test(msg.text())) errors.push(msg.text()); });
  const base = process.env.TEST_BASE_URL || 'http://127.0.0.1:5173';
  const settle = async () => { await page.waitForLoadState('networkidle'); };
  const choose = async (label, scope = page.locator('.app-language-control')) => {
   await scope.getByRole('button', { name: ({ KO: '한국어', EN: 'English', JP: '日本語' })[label], exact: true }).click();
   assert.equal(await page.locator('html').getAttribute('lang'), ({ KO: 'ko', EN: 'en', JP: 'ja' })[label]);
  };
  const checkUi = async (locale) => {
   let text = await page.locator('body').innerText();
   text += await page.locator('input[placeholder],textarea[placeholder]').evaluateAll(nodes => nodes.map(n => n.placeholder).join('\n'));
   assert(!/\bm\d{3}\b/.test(text), 'Unresolved translation key');
   if (locale !== 'KO') {
    text = text.replaceAll(original, '').replaceAll('한국어', '').replaceAll('12가3456', '');
    assert(!/[가-힣]/.test(text), 'Untranslated Korean: ' + text.match(/[^\n]*[가-힣][^\n]*/)?.[0]);
   }
  };
  const routes = ['/', '/login', '/signup', '/dashboard', '/monitoring', '/sites', '/equipments', '/maintenance', '/boards', '/boards/write', '/boards/1', '/boards/1/edit', '/admin', '/user', '/qna', '/qna/write', '/qna/1', '/qna/1/edit'];
  for (const route of routes) {
   await page.goto(base + route); await settle();
   const before = requests.length;
   for (const label of ['KO', 'EN', 'JP']) { await choose(label); await checkUi(label); }
   assert.equal(requests.length, before, 'Locale switch refetched data: ' + route);
   if (['/dashboard','/monitoring','/sites','/equipments','/maintenance','/boards','/boards/1','/admin','/user','/qna','/qna/1'].includes(route)) assert((await page.locator('body').innerText()).includes(original), 'Dynamic data changed: ' + route);
   console.log('PASS three languages:', route);
  }
  await page.reload(); await settle();
  assert.equal(await page.locator('html').getAttribute('lang'), 'ja');
  assert.equal(await page.evaluate(() => localStorage.getItem('language')), 'ja');
  console.log('PASS reload persistence and document language');

  await page.goto(base + '/boards/write'); await settle(); await choose('KO');
  await page.locator('#board-title').fill(original); await page.locator('#board-content').fill(original);
  for (const label of ['EN','JP','KO']) {
   await choose(label);
   assert.equal(await page.locator('#board-title').inputValue(), original);
   assert.equal(await page.locator('#board-content').inputValue(), original);
  }
  console.log('PASS unsaved form values preserved');

  // Already-visible local error messages must update without repeating the request.
  failBoards = true;
  await page.goto(base + '/boards'); await settle(); await choose('EN');
  assert((await page.locator('body').innerText()).includes('Failed to load posts.'));
  await choose('JP'); assert((await page.locator('body').innerText()).includes('投稿を取得できませんでした。'));
  failBoards = false;
  console.log('PASS reactive existing error message');

  for (const route of ['/monitoring','/sites','/equipments','/maintenance','/boards','/qna']) {
   emptyLists = true;
   await page.goto(base + route); await settle();
   for (const label of ['EN','JP']) { await choose(label); await checkUi(label); }
  }
  emptyLists = false;
  console.log('PASS empty states');

  await page.goto(base + '/equipments'); await settle(); await choose('EN');
  await page.locator('.search-panel select').nth(1).selectOption('WARNING');
  await choose('JP');
  assert.equal(await page.locator('.search-panel select').nth(1).inputValue(), 'WARNING');
  await choose('EN');
  await page.getByRole('button', { name: 'Search', exact: true }).click(); await settle();
  assert(requests.some(r => new URL(r.url).searchParams.get('status') === 'WARNING'));
  await page.locator('tbody').getByRole('button', { name: 'Details', exact: true }).click(); await settle();
  await choose('JP'); await checkUi('JP');
  await choose('EN');
  const confirm = page.waitForEvent('dialog').then(async dialog => {
   assert.equal(dialog.message(), 'Delete this equipment and its status history?');
   await dialog.dismiss();
  });
  await Promise.all([confirm, page.getByRole('button', { name: 'Delete equipment', exact: true }).click()]);
  console.log('PASS equipment details and translated confirm');

  await page.goto(base + '/sites'); await settle(); await choose('EN');
  await page.locator('tbody').getByRole('button', { name: 'Details', exact: true }).click(); await settle();
  await choose('JP'); await checkUi('JP');
  console.log('PASS site detail modal');

  await page.goto(base + '/monitoring'); await settle(); await choose('EN');
  await page.locator('.event-row').first().click(); await settle();
  await choose('JP'); await checkUi('JP');
  assert((await page.locator('body').innerText()).includes('イベント詳細情報'));
  console.log('PASS monitoring detail modal');

  await page.goto(base + '/user'); await settle(); await choose('JP');
  const alert = page.waitForEvent('dialog').then(async dialog => {
   assert.equal(dialog.message(), 'パスワード変更機能は準備中です。');
   await dialog.accept();
  });
  await Promise.all([alert, page.locator('.security-button').click()]);
  console.log('PASS translated alert');

  await page.goto(base + '/maintenance'); await settle(); await choose('EN');
  await page.getByRole('button', { name: '+ Report incident', exact: true }).click(); await settle();
  const modal = page.locator('dialog[open]');
  await modal.locator('input[maxlength="100"]').fill(original);
  await choose('JP', modal); await checkUi('JP');
  assert.equal(await modal.locator('input[maxlength="100"]').inputValue(), original);
  await choose('EN', modal);
  await modal.getByRole('button', { name: 'Close', exact: true }).click();
  await page.locator('tbody').getByRole('button', { name: 'Details', exact: true }).click(); await settle();
  await choose('JP', page.locator('dialog[open]')); await checkUi('JP');
  console.log('PASS native dialogs remain open and editable across languages');

  await page.goto(base + '/qna/1/edit'); await settle(); await choose('EN');
  assert.equal(await page.locator('select').inputValue(), 'GENERAL');
  assert.equal(await page.locator('textarea').inputValue(), original);
  await choose('JP');
  assert.equal(await page.locator('select').inputValue(), 'GENERAL');
  assert.equal(await page.locator('textarea').inputValue(), original);
  await page.goto(base + '/qna/1'); await settle(); await choose('EN');
  assert.equal(await page.locator('textarea').inputValue(), original);
  await choose('JP');
  assert.equal(await page.locator('textarea').inputValue(), original);
  console.log('PASS Q&A categories and original question/answer content preserved');

  assert(requests.filter(r => r.method !== 'OPTIONS').every(r => r.authorization === 'Bearer test-token'), 'JWT header changed');
  assert(requests.every(r => ['GET','OPTIONS'].includes(r.method)), 'Unexpected API mutation');
  assert.deepEqual(errors, []);
  await page.goto(base + '/login'); await settle(); await choose('EN');
  await page.setViewportSize({ width: 390, height: 844 });
  assert(await page.locator('.app-language-control').isVisible());
  if (process.env.TEST_SCREENSHOT) await page.screenshot({ path: process.env.TEST_SCREENSHOT, fullPage: true });
  console.log('PASS no runtime/i18n warnings, JWT preserved, mobile language control visible');
 } finally { await browser.close(); }
})().catch(error => { console.error(error); process.exitCode = 1; });
