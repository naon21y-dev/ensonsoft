// Start Vite; set PLAYWRIGHT_MODULE to your Playwright installation if needed.
// All API traffic is intercepted: no attendance is written to a real server.
const { chromium } = require(process.env.PLAYWRIGHT_MODULE || 'playwright');
const assert = require('node:assert/strict');
const { mkdirSync } = require('node:fs');
const { join } = require('node:path');
const screenshots = process.env.TEST_SCREENSHOT_DIR || 'dist';
mkdirSync(screenshots, { recursive: true });
const base = process.env.TEST_BASE_URL || 'http://127.0.0.1:5173';
const date = '2026-09-29', zone = 'Asia/Seoul', serverTime = '2026-09-29T01:00:00Z';
const missing = { id: null, memberId: 1, name: '직원 원문', username: 'employee', workDate: date, checkInAt: null, checkOutAt: null, workedSeconds: 0, workType: null, status: 'NOT_CHECKED_IN', notes: '' };
(async () => {
  const browser = await chromium.launch({ headless: true, channel: process.env.BROWSER_CHANNEL || 'msedge' });
  try {
    const errors = [], requests = [];
    let record = { ...missing }, fail = false;
    async function context(role) {
      const ctx = await browser.newContext({ viewport: { width: 1440, height: 1000 } });
      await ctx.addInitScript(role => {
        localStorage.setItem('accessToken', 'fixture-token'); localStorage.setItem('username', 'employee');
        localStorage.setItem('role', role); localStorage.setItem('language', 'en');
      }, role);
      await ctx.route(url => url.pathname.startsWith('/api/'), async route => {
        const req = route.request(), url = new URL(req.url()), p = url.pathname;
        requests.push({ path: p, method: req.method(), body: req.postDataJSON(), query: url.search });
        if (fail) return route.fulfill({ status: 500, json: {} });
        let data;
        if (p.endsWith('/check-in')) {
          assert.deepEqual(Object.keys(req.postDataJSON()).sort(), ['notes', 'workType']);
          record = { ...record, ...req.postDataJSON(), id: 9, checkInAt: serverTime, status: 'WORKING' }; data = record;
        } else if (p.endsWith('/check-out')) {
          assert(!req.postData()); record = { ...record, checkOutAt: serverTime, status: 'CHECKED_OUT' }; data = record;
        } else if (p.endsWith('/notes')) {
          record.notes = req.postDataJSON().notes; data = record;
        } else if (p.endsWith('/today')) data = { date, zone, serverTime, attendance: record, activeAttendance: record.status === 'WORKING' ? record : null };
        else if (p.endsWith('/history') || p.includes('/members/')) data = { memberId: 1, name: record.name, username: record.username, from: date, to: date, zone, serverTime, records: [record] };
        else if (p === '/api/admin/attendance') {
          let employees = [record, { ...missing, memberId: 2, name: '미출근 원문', username: 'absent' }];
          if (url.searchParams.get('status')) employees = employees.filter(r => r.status === url.searchParams.get('status'));
          if (url.searchParams.get('workType')) employees = employees.filter(r => r.workType === url.searchParams.get('workType'));
          if (url.searchParams.get('search')) employees = employees.filter(r => r.username.includes(url.searchParams.get('search')));
          data = { date, zone, serverTime, summary: { total: 2, working: 0, notCheckedIn: 1, checkedOut: 1, duty: 1, emergency: 0, substitute: 0 }, employees };
        } else throw new Error(`Unexpected API: ${p}`);
        await route.fulfill({ json: data });
      });
      const page = await ctx.newPage(); page.on('pageerror', e => errors.push(e.message));
      page.on('console', msg => { if (msg.type() === 'warning' && /intlify|Unhandled|not defined/.test(msg.text())) errors.push(msg.text()); });
      return page;
    }
    const page = await context('USER');
    const settle = async p => {
      await p.waitForLoadState('networkidle');
      await p.waitForFunction(() => !document.querySelector('.attendance-button:disabled'));
    };
    const choose = async (p, language, scope = '.app-language-control') => {
      await p.locator(scope).getByRole('button', { name: language, exact: true }).click();
    };
    await page.goto(base + '/attendance'); await settle(page);
    assert(await page.getByRole('heading', { name: 'My attendance', exact: true }).isVisible());
    assert.equal(await page.locator('a[href="/admin/attendance"]').count(), 0);
    await page.locator('.attendance-fields select').selectOption('DUTY');
    await page.locator('textarea').fill('현장 순찰 원문');
    await choose(page, '日本語');
    assert.equal(await page.locator('select').inputValue(), 'DUTY');
    assert.equal(await page.locator('textarea').inputValue(), '현장 순찰 원문');
    await choose(page, '한국어'); assert.equal(await page.locator('html').getAttribute('lang'), 'ko');
    await choose(page, 'English');
    await page.getByRole('button', { name: 'Clock in', exact: true }).click(); await settle(page);
    assert(await page.getByRole('button', { name: 'Clock out', exact: true }).isVisible());
    assert.equal(record.workType, 'DUTY');
    await page.locator('textarea').fill('인계 완료 원문');
    await page.getByRole('button', { name: 'Clock out', exact: true }).click(); await settle(page);
    assert.equal(record.notes, '인계 완료 원문');
    assert.equal(await page.getByRole('button', { name: 'Clock in', exact: true }).count(), 0);
    assert(await page.locator('.attendance-badge').first().innerText() === 'Clocked out');
    await page.setViewportSize({ width: 390, height: 844 });
    await page.screenshot({ path: join(screenshots, 'attendance-user-mobile.png'), fullPage: true });
    assert(await page.evaluate(() => document.documentElement.scrollWidth <= innerWidth), 'Mobile viewport overflow');
    await page.goto(base + '/admin/attendance'); await settle(page);
    assert.equal(new URL(page.url()).pathname, '/');
    const admin = await context('ADMIN'); await admin.goto(base + '/admin/attendance'); await settle(admin);
    assert(await admin.locator('a[href="/admin/attendance"]').isVisible());
    assert.equal(await admin.locator('.attendance-summary-card').count(), 7);
    assert.equal(await admin.locator('.attendance-table tbody tr').count(), 2);
    await admin.screenshot({ path: join(screenshots, 'attendance-admin-desktop.png'), fullPage: true });
    await admin.locator('.attendance-filters select').first().selectOption('NOT_CHECKED_IN');
    await admin.getByRole('button', { name: 'Search', exact: true }).click(); await settle(admin);
    assert.equal(await admin.locator('.attendance-table tbody tr').count(), 1);
    assert.equal(await admin.locator('.attendance-summary-card.total strong').innerText(), '2');
    await admin.getByRole('button', { name: 'Reset', exact: true }).click(); await settle(admin);
    await admin.getByRole('button', { name: 'Details', exact: true }).first().click(); await settle(admin);
    assert(await admin.locator('dialog').isVisible());
    await choose(admin, '日本語', 'dialog'); assert.equal(await admin.locator('html').getAttribute('lang'), 'ja');
    assert((await admin.locator('dialog').innerText()).includes('인계 완료 원문'));
    await choose(admin, '한국어', 'dialog'); await choose(admin, 'English', 'dialog');
    await admin.getByRole('button', { name: 'Close', exact: true }).click();
    fail = true;
    await admin.getByRole('button', { name: 'Refresh', exact: true }).click(); await settle(admin);
    assert((await admin.getByRole('alert').innerText()).includes('failed'));
    await choose(admin, '日本語');
    assert(!(await admin.getByRole('alert').innerText()).includes('failed'));
    assert(!/attendance\.|undefined/.test(await admin.locator('.attendance-page').innerText()));
    assert.deepEqual(errors, []);
    console.log(`Attendance browser checks passed: USER lifecycle, server-time payloads, ADMIN filters/details, route guard, KO/EN/JP, mobile, errors (${requests.length} mocked requests).`);
  } finally { await browser.close(); }
})().catch(e => { console.error(e); process.exitCode = 1; });
