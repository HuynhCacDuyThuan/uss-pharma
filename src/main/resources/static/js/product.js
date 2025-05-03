
      // Tab switching functionality
      document.querySelectorAll('.tab-item').forEach(tab => {
          tab.addEventListener('click', function() {
              document.querySelectorAll('.tab-item').forEach(item => item.classList.remove('active'));
              tab.classList.add('active');
          });
      });
