// Task 03 - Contact Management System
// Prodigy InfoTech Internship
// Stores contacts in localStorage so they persist across page reloads.

const STORAGE_KEY = "task03_contacts";

const form = document.getElementById("contact-form");
const nameInput = document.getElementById("name");
const phoneInput = document.getElementById("phone");
const emailInput = document.getElementById("email");
const editIdInput = document.getElementById("edit-id");
const submitBtn = document.getElementById("submit-btn");
const cancelBtn = document.getElementById("cancel-btn");
const errorBox = document.getElementById("error");
const listEl = document.getElementById("contact-list");
const emptyEl = document.getElementById("empty");
const countEl = document.getElementById("count");
const searchEl = document.getElementById("search");

let contacts = loadContacts();
let searchTerm = "";

function loadContacts() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch (e) {
    return [];
  }
}

function saveContacts() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(contacts));
}

function showError(msg) {
  errorBox.textContent = msg;
  errorBox.style.display = "block";
}

function clearError() {
  errorBox.textContent = "";
  errorBox.style.display = "none";
}

function isValidEmail(email) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

function isValidPhone(phone) {
  // accept digits, spaces, +, -, ()
  const digits = phone.replace(/[^0-9]/g, "");
  return digits.length >= 7 && digits.length <= 15;
}

function getInitials(name) {
  return name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((p) => p[0].toUpperCase())
    .join("");
}

function resetForm() {
  form.reset();
  editIdInput.value = "";
  submitBtn.textContent = "Add Contact";
  cancelBtn.style.display = "none";
  clearError();
}

form.addEventListener("submit", function (e) {
  e.preventDefault();
  clearError();

  const name = nameInput.value.trim();
  const phone = phoneInput.value.trim();
  const email = emailInput.value.trim();

  if (!name || !phone || !email) {
    showError("All fields are required.");
    return;
  }
  if (!isValidPhone(phone)) {
    showError("Please enter a valid phone number (7-15 digits).");
    return;
  }
  if (!isValidEmail(email)) {
    showError("Please enter a valid email address.");
    return;
  }

  const editId = editIdInput.value;
  if (editId) {
    // update existing
    const idx = contacts.findIndex((c) => c.id === editId);
    if (idx !== -1) {
      contacts[idx] = { ...contacts[idx], name, phone, email };
    }
  } else {
    // add new
    contacts.push({
      id: Date.now().toString(),
      name,
      phone,
      email,
    });
  }

  saveContacts();
  resetForm();
  render();
});

cancelBtn.addEventListener("click", function () {
  resetForm();
});

searchEl.addEventListener("input", function (e) {
  searchTerm = e.target.value.trim().toLowerCase();
  render();
});

function startEdit(id) {
  const c = contacts.find((x) => x.id === id);
  if (!c) return;
  editIdInput.value = c.id;
  nameInput.value = c.name;
  phoneInput.value = c.phone;
  emailInput.value = c.email;
  submitBtn.textContent = "Save Changes";
  cancelBtn.style.display = "inline-block";
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function deleteContact(id) {
  const c = contacts.find((x) => x.id === id);
  if (!c) return;
  if (!confirm('Delete contact "' + c.name + '"?')) return;
  contacts = contacts.filter((x) => x.id !== id);
  saveContacts();
  // if we were editing this one, reset
  if (editIdInput.value === id) resetForm();
  render();
}

function render() {
  const filtered = contacts.filter(function (c) {
    if (!searchTerm) return true;
    return (
      c.name.toLowerCase().includes(searchTerm) ||
      c.phone.toLowerCase().includes(searchTerm) ||
      c.email.toLowerCase().includes(searchTerm)
    );
  });

  // sort alphabetically by name
  filtered.sort((a, b) => a.name.localeCompare(b.name));

  countEl.textContent = contacts.length + (contacts.length === 1 ? " contact" : " contacts");

  listEl.innerHTML = "";
  if (filtered.length === 0) {
    emptyEl.style.display = "block";
    emptyEl.textContent = contacts.length === 0
      ? "No contacts yet. Add your first one above."
      : "No contacts match your search.";
    return;
  }
  emptyEl.style.display = "none";

  for (const c of filtered) {
    const li = document.createElement("li");
    li.className = "item";
    li.innerHTML =
      '<div class="avatar">' + getInitials(c.name) + "</div>" +
      '<div class="info">' +
        '<div class="name"></div>' +
        '<div class="meta"></div>' +
      "</div>" +
      '<div class="row-actions">' +
        '<button class="btn ghost small" data-action="edit">Edit</button>' +
        '<button class="btn danger small" data-action="delete">Delete</button>' +
      "</div>";
    // safely set text
    li.querySelector(".name").textContent = c.name;
    li.querySelector(".meta").textContent = c.phone + " • " + c.email;
    li.querySelector('[data-action="edit"]').addEventListener("click", () => startEdit(c.id));
    li.querySelector('[data-action="delete"]').addEventListener("click", () => deleteContact(c.id));
    listEl.appendChild(li);
  }
}

render();
