// modules/landingpageAnimation.js

const landingpageAnimation = {

    go: function () {
        landingpageAnimation.init();
    },

    init: function () {
        const chart = document.querySelector('.statistics-chart');

        if (!chart) {
            return;
        }

        const numberElements = chart.querySelectorAll('.statistic-number');
        let animationHasStarted = false;

        /**
         * Formatiert eine Zahl nach deutscher Schreibweise.
         */
        function formatNumber(value, decimals, suffix, millions = false) {
            if (millions) {
                value = value / 1_000_000;
            }

            return value.toLocaleString('en-US', {
                minimumFractionDigits: decimals,
                maximumFractionDigits: decimals
            }) + suffix;
        }

        /**
         * Zählt eine einzelne Zahl weich hoch.
         */
        function animateNumber(element) {
            const targetValue = Number(element.dataset.value);
            const decimals = Number(element.dataset.decimals || 0);
            const suffix = element.dataset.suffix || '';
            const millions = element.dataset.millions === 'true';

            const duration = 1400;

            if (!Number.isFinite(targetValue)) {
                console.warn('Ungültiger Zahlenwert:', element.dataset.value);
                return;
            }

            const startTime = performance.now();

            function updateNumber(currentTime) {
                const elapsed = currentTime - startTime;
                const progress = Math.min(elapsed / duration, 1);

                const easedProgress = 1 - Math.pow(1 - progress, 3);
                const currentValue = targetValue * easedProgress;

                element.textContent = formatNumber(
                    currentValue,
                    decimals,
                    suffix,
                    millions
                );

                if (progress < 1) {
                    requestAnimationFrame(updateNumber);
                } else {
                    element.textContent = formatNumber(
                        targetValue,
                        decimals,
                        suffix,
                        millions
                    );
                }
            }

            requestAnimationFrame(updateNumber);
        }

        /**
         * Startet alle Zahlen zeitlich versetzt.
         */
        function startChartAnimation() {
            if (animationHasStarted) {
                return;
            }

            animationHasStarted = true;
            chart.classList.add('is-visible');

            numberElements.forEach((element, index) => {
                const statistic = element.closest('.statistic');
                const customDelay = Number(statistic?.dataset.delay);
                const delay = Number.isFinite(customDelay)
                    ? customDelay
                    : index * 350;

                window.setTimeout(() => {
                    animateNumber(element);
                }, delay);
            });
        }

        /*
         * Animation startet erst, wenn der Chart sichtbar wird.
         */
        const observer = new IntersectionObserver(
            entries => {
                const entry = entries[0];

                if (entry.isIntersecting) {
                    startChartAnimation();
                    observer.disconnect();
                }
            },
            {
                threshold: 0.25
            }
        );

        observer.observe(chart);
    }
};