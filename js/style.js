const menuIcon = document.getElementById("menu-icon");
const navLinks = document.getElementById("nav-links");

menuIcon.addEventListener("click", () => {
  // Toggle the active class to show/hide the navigation links
  navLinks.classList.toggle("active");
});
