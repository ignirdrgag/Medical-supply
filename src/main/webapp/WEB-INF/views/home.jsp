<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<c:set var="activeNav" value="home" scope="request"/>

<header class="page-header">
    <div class="page-header-row">
        <div>
            <span class="page-kicker">Chaine d'approvisionnement medicale</span>
            <h1 class="page-title">Pilotage clair, tracable et securise du stock</h1>
            <p class="page-subtitle">
                MediSupply centralise les articles, les lots a date d'expiration, les commandes fournisseur et les mouvements
                de stock dans une base MySQL unique. Chaque role accede a un espace pense pour ses besoins.
            </p>
        </div>
        <div class="header-actions">
            <c:choose>
                <c:when test="${empty current}">
                    <a class="btn btn-primary" href="${ctx}/login">
                        Acceder a la plateforme
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M6.6665 4L10.6665 8L6.6665 12" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                    </a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-primary" href="${ctx}${current.homePath}">
                        Ouvrir mon espace
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                            <path d="M6.6665 4L10.6665 8L6.6665 12" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                    </a>
                </c:otherwise>
            </c:choose>
            <a class="btn btn-subtle" href="#espaces">Decouvrir les espaces</a>
        </div>
    </div>
</header>

<section class="grid grid-3">
    <article class="metric-card">
        <div class="metric-head">
            <span class="metric-label">Suivi par lot</span>
            <span class="metric-icon">
                <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M9 15.75V9" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
                    <path d="M13.5 6.75L9 9" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
                    <path d="M4.5 6.75L9 9" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
                    <path d="M2.25 6L9 2.25L15.75 6V12L9 15.75L2.25 12V6Z" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </span>
        </div>
        <div class="metric-value">FEFO</div>
        <div class="metric-foot">
            <span>Sorties par date d'expiration</span>
            <span>Priorite aux lots urgents</span>
        </div>
    </article>
    <article class="metric-card">
        <div class="metric-head">
            <span class="metric-label">Alertes intelligentes</span>
            <span class="metric-icon">
                <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M9 6V9" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
                    <path d="M9 12H9.0075" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
                    <path d="M7.5 3H10.5L15.75 12C16.0209 12.4676 16.163 12.995 16.163 13.5325C16.163 14.07 16.0209 14.5974 15.75 15.065C15.4791 15.5326 15.0908 15.922 14.624 16.194C14.1572 16.466 13.6306 16.61 13.0935 16.61H4.9065C4.3694 16.61 3.8428 16.466 3.376 16.194C2.9092 15.922 2.5209 15.5326 2.25 15.065C1.9791 14.5974 1.837 14.07 1.837 13.5325C1.837 12.995 1.9791 12.4676 2.25 12L7.5 3Z" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </span>
        </div>
        <div class="metric-value">24/7</div>
        <div class="metric-foot">
            <span>Seuils bas et perimptions</span>
            <span>Vue consolidee par role</span>
        </div>
    </article>
    <article class="metric-card">
        <div class="metric-head">
            <span class="metric-label">Acces controle</span>
            <span class="metric-icon">
                <svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <rect x="3.75" y="8.25" width="10.5" height="7.5" rx="1.5" stroke="currentColor" stroke-width="1.6"/>
                    <path d="M5.25 8.25V5.25C5.25 3.17893 6.92893 1.5 9 1.5C11.0711 1.5 12.75 3.17893 12.75 5.25V8.25" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>
                </svg>
            </span>
        </div>
        <div class="metric-value">RBAC</div>
        <div class="metric-foot">
            <span>Filtrage par role</span>
            <span>Sessions securisees</span>
        </div>
    </article>
</section>

<section class="panel" id="espaces">
    <header class="panel-header">
        <div class="panel-title-group">
            <span class="page-kicker">Espaces dedies</span>
            <h2 class="panel-title">Un acces clair pour chaque acteur</h2>
            <p class="panel-subtitle">Redirection automatique vers l'espace correspondant au compte connecte.</p>
        </div>
    </header>
    <div class="panel-body">
        <div class="grid grid-4">
            <article class="card-form" style="padding: 1.6rem;">
                <div class="card-form-header">
                    <div class="metric-icon"><svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg"><circle cx="9" cy="9" r="6.75" stroke="currentColor" stroke-width="1.6"/><path d="M9 5.25V9L11.25 10.5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg></div>
                    <div>
                        <h3 class="panel-title" style="font-size: 1.2rem;">Administrateur</h3>
                        <p class="panel-subtitle">Utilisateurs, fournisseurs, catalogue</p>
                    </div>
                </div>
                <p class="cell-title-sub">Gere les acces, le referentiel articles et l'annuaire fournisseurs.</p>
                <span class="pill pill-neutral">/admin/dashboard</span>
            </article>
            <article class="card-form" style="padding: 1.6rem;">
                <div class="card-form-header">
                    <div class="metric-icon"><svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M2.25 6L9 2.25L15.75 6V12L9 15.75L2.25 12V6Z" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/></svg></div>
                    <div>
                        <h3 class="panel-title" style="font-size: 1.2rem;">Agent de stock</h3>
                        <p class="panel-subtitle">Receptions et sorties</p>
                    </div>
                </div>
                <p class="cell-title-sub">Enregistre les lots, consomme par FEFO et suit les mouvements.</p>
                <span class="pill pill-neutral">/staff/dashboard</span>
            </article>
            <article class="card-form" style="padding: 1.6rem;">
                <div class="card-form-header">
                    <div class="metric-icon"><svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M3 9H15M9 3V15" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg></div>
                    <div>
                        <h3 class="panel-title" style="font-size: 1.2rem;">Responsable</h3>
                        <p class="panel-subtitle">Approvisionnement</p>
                    </div>
                </div>
                <p class="cell-title-sub">Anticipe les ruptures, cree et receptionne les commandes.</p>
                <span class="pill pill-neutral">/manager/dashboard</span>
            </article>
            <article class="card-form" style="padding: 1.6rem;">
                <div class="card-form-header">
                    <div class="metric-icon"><svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M9 2.25V15.75M2.25 9H15.75" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><rect x="2.25" y="2.25" width="13.5" height="13.5" rx="4" stroke="currentColor" stroke-width="1.4"/></svg></div>
                    <div>
                        <h3 class="panel-title" style="font-size: 1.2rem;">Medecin</h3>
                        <p class="panel-subtitle">Consultation du stock</p>
                    </div>
                </div>
                <p class="cell-title-sub">Consulte les quantites disponibles, les seuils et les produits en rupture.</p>
                <span class="pill pill-neutral">/doctor/dashboard</span>
            </article>
            <article class="card-form" style="padding: 1.6rem;">
                <div class="card-form-header">
                    <div class="metric-icon"><svg width="18" height="18" viewBox="0 0 18 18" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M9 10.5C10.6569 10.5 12 9.15685 12 7.5C12 5.84315 10.6569 4.5 9 4.5C7.34315 4.5 6 5.84315 6 7.5C6 9.15685 7.34315 10.5 9 10.5Z" stroke="currentColor" stroke-width="1.6"/><path d="M2.25 15C3.44344 12.7609 6.085 11.25 9 11.25C11.915 11.25 14.5566 12.7609 15.75 15" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/></svg></div>
                    <div>
                        <h3 class="panel-title" style="font-size: 1.2rem;">Fournisseur</h3>
                        <p class="panel-subtitle">Suivi des livraisons</p>
                    </div>
                </div>
                <p class="cell-title-sub">Visualise les lots fournis, les commandes et met a jour ses coordonnees.</p>
                <span class="pill pill-neutral">/supplier/dashboard</span>
            </article>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
