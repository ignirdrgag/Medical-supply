<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<section class="error-page">
    <span class="error-code">404</span>
    <h1>Page introuvable</h1>
    <p class="muted">L'adresse demandee n'existe pas.</p>
    <a class="btn" href="${ctx}/home">Retour a l'accueil</a>
</section>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
