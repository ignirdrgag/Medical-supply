<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<section class="error-page">
    <span class="error-code">403</span>
    <h1>Acces refuse</h1>
    <p class="muted">Votre role ne permet pas d'ouvrir cette page.</p>
    <a class="btn" href="${ctx}/home">Retour a l'accueil</a>
</section>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
