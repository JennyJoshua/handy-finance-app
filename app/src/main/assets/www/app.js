(function () {
  var el = document.getElementById('storage');
  try {
    var n = Number(localStorage.getItem('visits') || 0) + 1;
    localStorage.setItem('visits', String(n));
    el.textContent = 'Local storage works. Page opened ' + n + ' time(s).';
  } catch (e) {
    el.textContent = 'Local storage is not available.';
  }
})();
