package wekb.tenant

class TenantContext {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>()

    static void setTenant(String tenant) {
        currentTenant.set(tenant)
    }

    static String getTenant() {
        return currentTenant.get()
    }

    static void clear() {
        currentTenant.remove()
    }
}

