<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<section class="auth-hero">
    <div class="auth-content">
        <div>
            <span class="page-kicker">Authentification</span>
            <h1 class="page-title">Connexion a MediSupply</h1>
            <p class="page-subtitle">
                Accedez a votre espace selon votre role pour gerer le stock, les commandes et les approvisionnements.
            </p>
        </div>
        <div class="grid grid-2">
            <article class="metric-card">
                <div class="metric-head">
                    <span class="metric-label">Tracabilite</span>
                </div>
                <p class="cell-title-sub">Lots, dates d'expiration et mouvements centralises.</p>
            </article>
            <article class="metric-card">
                <div class="metric-head">
                    <span class="metric-label">Pilotage</span>
                </div>
                <p class="cell-title-sub">Alertes en temps reel pour anticiper les ruptures.</p>
            </article>
        </div>
    </div>
    <div class="auth-card">
        <div class="card-form-header">
            <h2 class="panel-title" style="font-size: 1.6rem;">Se connecter</h2>
            <p class="panel-subtitle">Utilisez vos identifiants pour acceder a votre espace.</p>
        </div>
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                <div>
                    <strong>Erreur de connexion</strong>
                    <div class="cell-title-sub"><c:out value="${error}"/></div>
                </div>
            </div>
        </c:if>
        <form method="post" action="${ctx}/login" class="auth-form">
            <div class="field">
                <label class="field-label" for="username">Nom d'utilisateur</label>
                <input class="field-input" id="username" type="text" name="username" value="<c:out value='${username}'/>" placeholder="ex. admin" required autofocus>
            </div>
            <div class="field">
                <label class="field-label" for="password">Mot de passe</label>
            <input class="field-input" id="password" type="password" name="password" placeholder="********" required>
            </div>
            <button class="btn btn-primary" type="submit" style="justify-content: center; padding: 0.8rem 1.2rem;">
                Se connecter
                <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M6.6665 4L10.6665 8L6.6665 12" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </button>
        </form>
    </div>
</section>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
