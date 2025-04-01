UPDATE vendor_electronic_billing set veb_electronic_billing_rv_fk = (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = vendor_electronic_billing.veb_electronic_billing_rv_fk limit 1) and rdv_is_hard_data = true limit 1)
where veb_electronic_billing_rv_fk in (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = vendor_electronic_billing.veb_electronic_billing_rv_fk) and rdv_is_hard_data = false);

UPDATE vendor_electronic_delivery_delay set vedd_electronic_delivery_delay_rv_fk = (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = vedd_electronic_delivery_delay_rv_fk limit 1) and rdv_is_hard_data = true limit 1)
where vedd_electronic_delivery_delay_rv_fk in (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = vedd_electronic_delivery_delay_rv_fk) and rdv_is_hard_data = false);

UPDATE vendor_invoice_dispatch set vid_invoice_dispatch_rv_fk = (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = vid_invoice_dispatch_rv_fk limit 1) and rdv_is_hard_data = true limit 1)
where vid_invoice_dispatch_rv_fk in (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = vid_invoice_dispatch_rv_fk) and rdv_is_hard_data = false);

UPDATE vendor_library_system set vls_supported_library_system_rv_fk = (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = vls_supported_library_system_rv_fk limit 1) and rdv_is_hard_data = true limit 1)
where vls_supported_library_system_rv_fk in (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = vls_supported_library_system_rv_fk) and rdv_is_hard_data = false);


UPDATE provider_electronic_billing set peb_electronic_billing_rv_fk = (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = peb_electronic_billing_rv_fk limit 1) and rdv_is_hard_data = true limit 1)
where peb_electronic_billing_rv_fk in (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = peb_electronic_billing_rv_fk) and rdv_is_hard_data = false);

UPDATE provider_invoice_dispatch set pid_invoice_dispatch_rv_fk = (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = pid_invoice_dispatch_rv_fk limit 1) and rdv_is_hard_data = true limit 1)
where pid_invoice_dispatch_rv_fk in (select rdv_id from refdata_value where rdv_value = (select rdv_value from refdata_value where rdv_id = pid_invoice_dispatch_rv_fk) and rdv_is_hard_data = false);