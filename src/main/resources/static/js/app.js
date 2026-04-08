/* ── Constants ── */
const API = '/api/products';

const CATEGORIES = [
  'ELECTRONICS', 'FASHION', 'HOME_AND_KITCHEN', 'SPORTS',
  'BEAUTY', 'BOOKS', 'TOYS', 'FOOD_AND_GROCERY',
  'AUTOMOTIVE', 'HEALTH', 'TRAVEL', 'FINANCE'
];

/* ── State ── */
let allProducts = [];
let editingSku  = null;
let deleteSku   = null;

/* ── DOM refs ── */
const main          = document.querySelector('.main');
const panel         = document.getElementById('panel');
const panelTitle    = document.getElementById('panel-title');
const form          = document.getElementById('product-form');
const productsBody  = document.getElementById('products-body');
const searchInput   = document.getElementById('search');
const filterCat     = document.getElementById('filter-category');
const filterType    = document.getElementById('filter-type');
const showInactive  = document.getElementById('show-inactive');
const toast         = document.getElementById('toast');
const modalBackdrop = document.getElementById('modal-backdrop');
const modalMsg      = document.getElementById('modal-msg');
const subSection    = document.getElementById('subscription-section');
const productTypeField = document.getElementById('field-product-type');

/* ── Bootstrap ── */
populateCategoryOptions();
loadProducts();
initTypeTabs();

/* ── Event listeners ── */
document.getElementById('btn-add').addEventListener('click', openAddPanel);
document.getElementById('btn-close').addEventListener('click', closePanel);
document.getElementById('btn-cancel').addEventListener('click', closePanel);
document.getElementById('modal-cancel').addEventListener('click', closeModal);
document.getElementById('modal-confirm').addEventListener('click', confirmDelete);
form.addEventListener('submit', handleSubmit);
searchInput.addEventListener('input', renderTable);
filterCat.addEventListener('change', renderTable);
filterType.addEventListener('change', renderTable);
showInactive.addEventListener('change', renderTable);

/* ── Type tabs ── */
function initTypeTabs() {
  document.querySelectorAll('.type-tab').forEach(tab => {
    tab.addEventListener('click', () => {
      document.querySelectorAll('.type-tab').forEach(t => t.classList.remove('active'));
      tab.classList.add('active');
      const type = tab.dataset.type;
      productTypeField.value = type;
      subSection.classList.toggle('hidden', type !== 'SUBSCRIPTION');
      document.getElementById('label-price').innerHTML =
        type === 'SUBSCRIPTION'
          ? 'Recurring Price ($) <span class="req">*</span>'
          : 'Price ($) <span class="req">*</span>';
    });
  });
}

function setActiveTab(type) {
  document.querySelectorAll('.type-tab').forEach(t => {
    t.classList.toggle('active', t.dataset.type === type);
  });
  productTypeField.value = type;
  subSection.classList.toggle('hidden', type !== 'SUBSCRIPTION');
  document.getElementById('label-price').innerHTML =
    type === 'SUBSCRIPTION'
      ? 'Recurring Price ($) <span class="req">*</span>'
      : 'Price ($) <span class="req">*</span>';
}

/* ── Category helpers ── */
function populateCategoryOptions() {
  const formSelect = document.getElementById('field-category');
  CATEGORIES.forEach(cat => {
    [filterCat, formSelect].forEach(sel => {
      const opt = document.createElement('option');
      opt.value = cat;
      opt.textContent = formatEnum(cat);
      sel.appendChild(opt);
    });
  });
}

/* ── API calls ── */
async function loadProducts() {
  try {
    const res = await fetch(API + '?includeInactive=true');
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    allProducts = await res.json();
    renderTable();
  } catch (e) {
    showToast('Failed to load products: ' + e.message, 'error');
    productsBody.innerHTML = `<tr><td colspan="10" class="empty">Could not load products.</td></tr>`;
  }
}

async function handleSubmit(e) {
  e.preventDefault();
  if (!validateForm()) return;

  const payload = buildPayload();
  const isEdit  = editingSku !== null;
  const url     = isEdit ? `${API}/${editingSku}` : API;
  const method  = isEdit ? 'PUT' : 'POST';

  try {
    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || `HTTP ${res.status}`);
    }
    showToast(isEdit ? 'Product updated.' : 'Product created.', 'success');
    closePanel();
    await loadProducts();
  } catch (e) {
    showToast('Save failed: ' + e.message, 'error');
  }
}

async function confirmDelete() {
  if (deleteSku === null) return;
  try {
    const res = await fetch(`${API}/${deleteSku}`, { method: 'DELETE' });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    showToast('Product deleted.', 'success');
    closeModal();
    await loadProducts();
  } catch (e) {
    showToast('Delete failed: ' + e.message, 'error');
    closeModal();
  }
}

/* ── Table rendering ── */
function renderTable() {
  const query     = searchInput.value.trim().toLowerCase();
  const catFilter = filterCat.value;
  const typeFilter = filterType.value;
  const inclInact = showInactive.checked;

  const filtered = allProducts.filter(p => {
    if (!inclInact && !p.active) return false;
    if (catFilter && p.category !== catFilter) return false;
    if (typeFilter && p.productType !== typeFilter) return false;
    if (query && !p.name.toLowerCase().includes(query) && !p.sku.toLowerCase().includes(query)) return false;
    return true;
  });

  if (filtered.length === 0) {
    productsBody.innerHTML = `<tr><td colspan="10" class="empty">No products found.</td></tr>`;
    return;
  }

  productsBody.innerHTML = filtered.map(p => {
    const isSub = p.productType === 'SUBSCRIPTION';
    const typeLabel = isSub ? 'Subscription' : 'Standard';
    const typeBadge = isSub ? 'badge-subscription' : 'badge-standard';
    const billing = isSub && p.billingCycle ? formatEnum(p.billingCycle) : '—';
    const trial   = isSub && p.trialDays != null ? `${p.trialDays}d` : '—';
    const priceLabel = isSub ? `$${Number(p.price).toFixed(2)}/cycle` : `$${Number(p.price).toFixed(2)}`;

    return `
    <tr>
      <td><code>${escHtml(p.sku)}</code></td>
      <td><strong>${escHtml(p.name)}</strong></td>
      <td><span class="badge ${typeBadge}">${typeLabel}</span></td>
      <td><span class="badge badge-cat">${formatEnum(p.category || '')}</span></td>
      <td>${priceLabel}</td>
      <td>${billing}</td>
      <td>${trial}</td>
      <td>${ageRange(p)}</td>
      <td><span class="badge ${p.active ? 'badge-active' : 'badge-inactive'}">${p.active ? 'Active' : 'Inactive'}</span></td>
      <td>
        <div class="row-actions">
          <button class="btn btn-ghost btn-sm" onclick="openEditPanel('${escHtml(p.sku)}')">Edit</button>
          <button class="btn btn-danger btn-sm" onclick="openDeleteModal('${escHtml(p.sku)}', '${escHtml(p.name)}')">Delete</button>
        </div>
      </td>
    </tr>`;
  }).join('');
}

/* ── Panel open/close ── */
function openAddPanel() {
  editingSku = null;
  panelTitle.textContent = 'Add Product';
  document.getElementById('btn-submit').textContent = 'Create Product';
  form.reset();
  setActiveTab('STANDARD');
  document.getElementById('field-sku').disabled   = false;
  document.getElementById('field-active').checked  = true;
  clearErrors();
  panel.classList.add('open');
  main.classList.add('panel-open');
  document.getElementById('field-sku').focus();
}

function openEditPanel(sku) {
  const p = allProducts.find(x => x.sku === sku);
  if (!p) return;
  editingSku = sku;
  panelTitle.textContent = 'Edit Product';
  document.getElementById('btn-submit').textContent = 'Save Changes';

  setActiveTab(p.productType || 'STANDARD');

  const skuField = document.getElementById('field-sku');
  skuField.value    = p.sku;
  skuField.disabled = true;

  // Type tabs locked on edit
  document.querySelectorAll('.type-tab').forEach(t => t.disabled = true);

  document.getElementById('field-name').value          = p.name || '';
  document.getElementById('field-description').value   = p.description || '';
  document.getElementById('field-price').value         = p.price ?? '';
  document.getElementById('field-category').value      = p.category || '';
  document.getElementById('field-min-age').value       = p.minAge ?? '';
  document.getElementById('field-max-age').value       = p.maxAge ?? '';
  document.getElementById('field-gender').value        = p.targetGender || '';
  document.getElementById('field-income').value        = p.minIncomeLevel || '';
  document.getElementById('field-tags').value          = (p.tags || []).join(', ');
  document.getElementById('field-image-url').value     = p.imageUrl || '';
  document.getElementById('field-active').checked      = p.active;

  if (p.productType === 'SUBSCRIPTION') {
    document.getElementById('field-billing-cycle').value = p.billingCycle || '';
    document.getElementById('field-trial-days').value   = p.trialDays ?? '';
  }

  clearErrors();
  panel.classList.add('open');
  main.classList.add('panel-open');
}

function closePanel() {
  panel.classList.remove('open');
  main.classList.remove('panel-open');
  editingSku = null;
  form.reset();
  document.getElementById('field-sku').disabled = false;
  document.querySelectorAll('.type-tab').forEach(t => t.disabled = false);
  setActiveTab('STANDARD');
  clearErrors();
}

/* ── Delete modal ── */
function openDeleteModal(sku, name) {
  deleteSku = sku;
  modalMsg.textContent = `"${name}" (${sku}) will be deactivated and hidden from customers.`;
  modalBackdrop.classList.remove('hidden');
}

function closeModal() {
  deleteSku = null;
  modalBackdrop.classList.add('hidden');
}

/* ── Form helpers ── */
function buildPayload() {
  const type    = productTypeField.value;
  const tagsRaw = document.getElementById('field-tags').value;
  const tags    = tagsRaw.split(',').map(t => t.trim()).filter(Boolean);
  const skuVal  = document.getElementById('field-sku').value.trim().toUpperCase();

  const base = {
    productType:    type,
    sku:            skuVal || null,
    name:           document.getElementById('field-name').value.trim(),
    description:    document.getElementById('field-description').value.trim() || null,
    price:          parseFloat(document.getElementById('field-price').value),
    category:       document.getElementById('field-category').value || null,
    minAge:         intOrNull(document.getElementById('field-min-age').value),
    maxAge:         intOrNull(document.getElementById('field-max-age').value),
    targetGender:   document.getElementById('field-gender').value || null,
    minIncomeLevel: document.getElementById('field-income').value || null,
    tags,
    imageUrl:       document.getElementById('field-image-url').value.trim() || null,
    active:         document.getElementById('field-active').checked
  };

  if (type === 'SUBSCRIPTION') {
    base.billingCycle = document.getElementById('field-billing-cycle').value || null;
    base.trialDays    = intOrNull(document.getElementById('field-trial-days').value);
  }

  return base;
}

function validateForm() {
  clearErrors();
  let valid = true;

  const sku = document.getElementById('field-sku').value.trim();
  if (sku && !/^[A-Za-z0-9-]{1,20}$/.test(sku)) {
    setError('err-sku', 'SKU must be alphanumeric with optional hyphens (max 20 chars).');
    valid = false;
  }

  if (!document.getElementById('field-name').value.trim()) {
    setError('err-name', 'Name is required.');
    valid = false;
  }

  const price = document.getElementById('field-price').value;
  if (price === '' || isNaN(parseFloat(price)) || parseFloat(price) < 0) {
    setError('err-price', 'Enter a valid price (≥ 0).');
    valid = false;
  }

  if (!document.getElementById('field-category').value) {
    setError('err-category', 'Select a category.');
    valid = false;
  }

  if (productTypeField.value === 'SUBSCRIPTION') {
    if (!document.getElementById('field-billing-cycle').value) {
      setError('err-billing-cycle', 'Select a billing cycle.');
      valid = false;
    }
  }

  return valid;
}

function setError(id, msg) {
  const el = document.getElementById(id);
  if (el) el.textContent = msg;
}

function clearErrors() {
  document.querySelectorAll('.field-error').forEach(el => el.textContent = '');
}

/* ── Toast ── */
let toastTimer;
function showToast(msg, type = '') {
  clearTimeout(toastTimer);
  toast.textContent = msg;
  toast.className = 'toast' + (type ? ' ' + type : '');
  toastTimer = setTimeout(() => toast.classList.add('hidden'), 3500);
}

/* ── Utilities ── */
function formatEnum(str) {
  return str.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
}

function ageRange(p) {
  if (!p.minAge && !p.maxAge) return 'Any';
  if (!p.minAge) return `≤ ${p.maxAge}`;
  if (!p.maxAge) return `${p.minAge}+`;
  return `${p.minAge}–${p.maxAge}`;
}

function intOrNull(val) {
  const n = parseInt(val, 10);
  return isNaN(n) ? null : n;
}

function escHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}
