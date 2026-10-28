<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<c:choose>
    <c:when test="${noSupplier}">
        <div class="page-heading">
            <div>
                <span class="eyebrow">Fournisseur</span>
                <h1>Compte non associe</h1>
                <p class="muted">Votre compte n'est lie a aucun fournisseur. Contactez l'administrateur.</p>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="page-heading">
            <div>
                <span class="eyebrow">Fournisseur</span>
                <h1><c:out value="${supplier.name}"/></h1>
                <p class="muted">Vos lots fournis et l'etat de vos commandes.</p>
            </div>
            <span class="role-badge role-supplier">Espace fournisseur</span>
        </div>

        <section class="stats-grid">
            <article class="card stat"><span>Lots fournis</span><strong>${lotCount}</strong><small>lots en stock</small></article>
            <article class="card stat"><span>Quantite totale</span><strong>${totalQuantity}</strong><small>unites en stock</small></article>
            <article class="card stat"><span>Expiration proche</span><strong>${expiringSoon}</strong><small>sous 30 jours</small></article>
            <article class="card stat"><span>Commandes en attente</span><strong>${pendingOrders}</strong><small>a livrer</small></article>
        </section>

        <section class="section">
            <div class="grid grid-2">
                <article class="card">
                    <h2>Coordonnees</h2>
                    <dl class="details">
                        <dt>Contact</dt><dd><c:out value="${supplier.contact}"/></dd>
                        <dt>E-mail</dt><dd><c:out value="${supplier.email}"/></dd>
                    </dl>
                    <form method="post" action="${ctx}/supplier/profile" class="form-grid">
                        <label>Nouveau contact<input type="text" name="contact" value="<c:out value='${supplier.contact}'/>" required></label>
                        <label>Nouvel e-mail<input type="email" name="email" value="<c:out value='${supplier.email}'/>" required></label>
                        <div class="span-2"><button class="btn" type="submit">Mettre a jour</button></div>
                    </form>
                </article>

                <article class="card">
                    <h2>Lots en stock</h2>
                    <c:choose>
                        <c:when test="${empty lots}">
                            <p class="muted">Aucun lot en stock.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="table-wrap compact">
                                <table>
                                    <thead><tr><th>Article</th><th>Quantite</th><th>Expiration</th><th>Etat</th></tr></thead>
                                    <tbody>
                                    <c:forEach var="lot" items="${lots}">
                                        <tr>
                                            <td><strong><c:out value="${lot.itemName}"/></strong></td>
                                            <td>${lot.quantity}</td>
                                            <td><c:out value="${lot.expirationDisplay}"/></td>
                                            <td>
                                                <span class="pill ${lot.expired ? 'pill-danger' : (lot.expiringSoon ? 'pill-warn' : 'pill-ok')}">
                                                    ${lot.expired ? 'Perime' : (lot.expiringSoon ? 'Bientot' : 'OK')}
                                                </span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </article>
            </div>
        </section>

        <section class="section">
            <div class="section-heading">
                <h2>Commandes</h2>
                <p>Commandes passees aupres de votre entreprise.</p>
            </div>
            <div class="table-wrap">
                <table>
                    <thead><tr><th>No</th><th>Date</th><th>Articles</th><th>Quantite</th><th>Statut</th></tr></thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty orders}">
                            <tr><td colspan="5" class="muted center">Aucune commande.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="order" items="${orders}">
                                <tr>
                                    <td><strong>#${order.id}</strong></td>
                                    <td><c:out value="${order.orderDateDisplay}"/></td>
                                    <td>
                                        <c:forEach var="line" items="${order.lines}" varStatus="loop">
                                            <c:out value="${line.itemName}"/> (${line.quantity})<c:if test="${!loop.last}">, </c:if>
                                        </c:forEach>
                                    </td>
                                    <td>${order.totalQuantity}</td>
                                    <td>
                                        <span class="pill ${order.pending ? 'pill-warn' : 'pill-ok'}">
                                            <c:out value="${order.statusLabel}"/>
                                        </span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </section>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
