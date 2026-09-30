const pages = [
  ["overview", "Admin overview"],
  ["downloads", "Downloads"],
  ["app-status", "App status"],
  ["product", "Product information"],
  ["analytics", "Analytics"]
];

function showPage(name) {
  const page = pages.find(([id]) => id === name);
  if (!page) return;
  document.querySelectorAll(".page").forEach((item) => {
    item.classList.toggle("active", item.id === name);
  });
  document.querySelectorAll(".nav[data-page]").forEach((button) => {
    const active = button.dataset.page === name;
    button.classList.toggle("active", active);
    if (active) button.setAttribute("aria-current", "page");
    else button.removeAttribute("aria-current");
  });
  document.querySelector("#page-title").textContent = page[1];
  history.replaceState(null, "", "#" + name);
}

document.querySelectorAll(".nav[data-page]").forEach((button) => {
  button.addEventListener("click", () => showPage(button.dataset.page));
});

const requestedPage = window.location.hash.slice(1);
if (pages.some(([id]) => id === requestedPage)) showPage(requestedPage);
