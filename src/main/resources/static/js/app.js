(() => {
    "use strict";

    const navToggle = document.querySelector("[data-nav-toggle]");
    const navMenu = document.querySelector("[data-nav-menu]");

    if (navToggle && navMenu) {
        navToggle.addEventListener("click", () => {
            const open = navMenu.classList.toggle("open");
            navToggle.setAttribute("aria-expanded", String(open));
        });
    }

    document.querySelectorAll("input[data-no-future]").forEach((input) => {
        const now = new Date();
        const localToday = new Date(now.getTime() - now.getTimezoneOffset() * 60000)
            .toISOString()
            .slice(0, 10);
        input.max = localToday;
    });

    document.querySelectorAll("form[data-date-range]").forEach((form) => {
        const start = form.querySelector("[data-date-start]");
        const end = form.querySelector("[data-date-end]");
        const error = form.querySelector("[data-date-error]");
        const message = form.dataset.dateMessage || "End date cannot be before start date.";

        if (!start || !end) return;

        const validateRange = () => {
            if (start.value) {
                end.min = start.value;
            } else {
                end.removeAttribute("min");
            }

            const invalid = Boolean(start.value && end.value && end.value < start.value);
            end.setCustomValidity(invalid ? message : "");
            end.classList.toggle("is-invalid", invalid);

            if (error) {
                error.textContent = invalid ? message : "";
            }
        };

        start.addEventListener("input", validateRange);
        start.addEventListener("change", validateRange);
        end.addEventListener("input", validateRange);
        end.addEventListener("change", validateRange);
        validateRange();
    });

    document.querySelectorAll("form.form-card").forEach((form) => {
        form.addEventListener("submit", (event) => {
            form.classList.add("was-validated");
            if (!form.checkValidity()) {
                event.preventDefault();
                const invalid = form.querySelector(":invalid");
                invalid?.focus();
            }
        });
    });

    // One reusable confirmation modal for every destructive delete link.
    const modal = document.getElementById("confirmModal");
    if (modal) {
        const confirmAction = modal.querySelector("[data-confirm-action]");
        const cancelButton = modal.querySelector("[data-confirm-cancel]");
        const title = modal.querySelector("#confirmTitle");
        const message = modal.querySelector("#confirmMessage");
        let lastTrigger = null;

        const closeModal = () => {
            modal.hidden = true;
            document.body.classList.remove("modal-open");
            if (confirmAction) confirmAction.href = "#";
            lastTrigger?.focus();
        };

        const openModal = (trigger) => {
            const entity = trigger.dataset.deleteEntity || "record";
            const name = trigger.dataset.deleteName?.trim();

            lastTrigger = trigger;
            if (confirmAction) confirmAction.href = trigger.href;
            if (title) title.textContent = `Delete ${entity}?`;
            if (message) {
                message.textContent = name
                    ? `You are about to delete “${name}”. This action cannot be undone.`
                    : `This ${entity} will be permanently deleted. This action cannot be undone.`;
            }

            modal.hidden = false;
            document.body.classList.add("modal-open");
            cancelButton?.focus();
        };

        document.querySelectorAll("[data-delete-trigger]").forEach((trigger) => {
            trigger.addEventListener("click", (event) => {
                event.preventDefault();
                openModal(trigger);
            });
        });

        cancelButton?.addEventListener("click", closeModal);

        modal.addEventListener("click", (event) => {
            if (event.target === modal) closeModal();
        });

        document.addEventListener("keydown", (event) => {
            if (event.key === "Escape" && !modal.hidden) closeModal();
        });
    }
})();
