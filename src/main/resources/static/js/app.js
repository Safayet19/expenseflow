(() => {
    "use strict";

    const localDateString = () => {
        const now = new Date();
        return new Date(now.getTime() - now.getTimezoneOffset() * 60000)
            .toISOString()
            .slice(0, 10);
    };

    const today = localDateString();

    const navToggle = document.querySelector("[data-nav-toggle]");
    const navMenu = document.querySelector("[data-nav-menu]");

    if (navToggle && navMenu) {
        navToggle.addEventListener("click", () => {
            const open = navMenu.classList.toggle("open");
            navToggle.setAttribute("aria-expanded", String(open));
        });
    }

    document.querySelectorAll("input[data-no-future]").forEach((input) => {
        input.max = today;
    });

    document.querySelectorAll("input[data-no-past]").forEach((input) => {
        input.min = today;
    });

    document.querySelectorAll("form[data-date-range]").forEach((form) => {
        const start = form.querySelector("[data-date-start]");
        const end = form.querySelector("[data-date-end]");
        const error = form.querySelector("[data-date-error]");
        const message = form.dataset.dateMessage || "End date cannot be before start date.";

        if (!start || !end) return;

        const validateRange = () => {
            const noPast = end.hasAttribute("data-no-past");
            if (start.value) {
                end.min = noPast && start.value < today ? today : start.value;
            } else if (noPast) {
                end.min = today;
            } else {
                end.removeAttribute("min");
            }

            const invalidRange = Boolean(start.value && end.value && end.value < start.value);
            const invalidPast = Boolean(noPast && end.value && end.value < today);
            const invalid = invalidRange || invalidPast;
            const activeMessage = invalidPast ? "Next contact date cannot already be in the past." : message;
            end.setCustomValidity(invalid ? activeMessage : "");
            end.classList.toggle("is-invalid", invalid);

            if (error) {
                error.textContent = invalid ? activeMessage : "";
            }
        };

        start.addEventListener("input", validateRange);
        start.addEventListener("change", validateRange);
        end.addEventListener("input", validateRange);
        end.addEventListener("change", validateRange);
        validateRange();
    });

    document.querySelectorAll("form[data-loan-rules]").forEach((form) => {
        const type = form.querySelector("[data-loan-type]");
        const itemName = form.querySelector("[data-item-name]");
        const amount = form.querySelector("[data-loan-amount]");
        const quantity = form.querySelector("[data-loan-quantity]");

        if (!type || !itemName || !amount || !quantity) return;

        const syncLoanRules = () => {
            const isMoney = type.value === "Money";

            amount.required = isMoney;
            amount.min = isMoney ? "0.01" : "0";

            itemName.required = Boolean(type.value) && !isMoney;
            quantity.required = Boolean(type.value) && !isMoney;
            quantity.min = !isMoney && type.value ? "1" : "0";
        };

        type.addEventListener("change", syncLoanRules);
        syncLoanRules();
    });

    document.querySelectorAll("form[data-reminder-form]").forEach((form) => {
        const date = form.querySelector("[data-reminder-date]");
        const time = form.querySelector("[data-reminder-time]");
        const completed = form.querySelector("[data-reminder-completed]");
        const error = form.querySelector("[data-reminder-error]");

        if (!date || !time || !completed) return;

        const validateReminder = () => {
            if (completed.checked) {
                date.removeAttribute("min");
                date.setCustomValidity("");
                time.setCustomValidity("");
                if (error) error.textContent = "";
                return;
            }

            date.min = today;
            let message = "";

            if (date.value && date.value < today) {
                message = "Pending reminder date cannot be in the past.";
                date.setCustomValidity(message);
                time.setCustomValidity("");
            } else {
                date.setCustomValidity("");

                if (date.value === today && time.value) {
                    const now = new Date();
                    const currentMinutes = now.getHours() * 60 + now.getMinutes();
                    const [hour, minute] = time.value.split(":").map(Number);
                    const selectedMinutes = hour * 60 + minute;

                    if (selectedMinutes < currentMinutes) {
                        message = "Pending reminder time cannot already be in the past.";
                        time.setCustomValidity(message);
                    } else {
                        time.setCustomValidity("");
                    }
                } else {
                    time.setCustomValidity("");
                }
            }

            date.classList.toggle("is-invalid", Boolean(date.validationMessage));
            time.classList.toggle("is-invalid", Boolean(time.validationMessage));
            if (error) error.textContent = message;
        };

        [date, time, completed].forEach((input) => {
            input.addEventListener("input", validateReminder);
            input.addEventListener("change", validateReminder);
        });
        validateReminder();
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

    const modal = document.getElementById("confirmModal");
    if (modal) {
        const confirmForm = modal.querySelector("[data-confirm-form]");
        const cancelButton = modal.querySelector("[data-confirm-cancel]");
        const title = modal.querySelector("#confirmTitle");
        const message = modal.querySelector("#confirmMessage");
        let lastTrigger = null;

        const closeModal = () => {
            modal.hidden = true;
            document.body.classList.remove("modal-open");
            if (confirmForm) confirmForm.action = "";
            lastTrigger?.focus();
        };

        const openModal = (trigger) => {
            const entity = trigger.dataset.deleteEntity || "record";
            const name = trigger.dataset.deleteName?.trim();
            const deleteUrl = trigger.dataset.deleteUrl || trigger.closest("form")?.action;

            if (!deleteUrl) return;

            lastTrigger = trigger;
            if (confirmForm) confirmForm.action = deleteUrl;
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
