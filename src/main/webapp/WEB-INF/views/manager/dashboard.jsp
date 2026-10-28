<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<div class="page-heading">
    <div>
        <span class="eyebrow">Responsable</span>
        <h1>Pilotage des stocks et des commandes</h1>
        <p class="muted">Anticipez les ruptures, les peremptions et les commandes fournisseur.</p>
    </div>
</div>

<section class="stats-grid">
    <article class="card stat"><span>Articles</span><strong>${itemCount}</strong><small>references suivies</small></article>
    <article class="card stat"><span>Fournisseurs</span><strong>${supplierCount}</strong><small>partenaires</small></article>
    <article class="card stat"><span>Commandes en attente</span><strong>${pendingOrders}</strong><small>a receptionner</small></article>
    <article class="card stat"><span>Mouvements</span><strong>${movementCount}</strong><small>sorties enregistrees</small></article>
</section>

<section class="section">
    <div class="grid grid-2">
        <article class="card">
            <h2>Articles sous le seuil</h2>
            <c:choose>
                <c:when test="${empty lowStockItems}">
                    <p class="muted">Aucun article sous le seuil.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap compact">
                        <table>
                            <thead><tr><th>Article</th><th>Utilisable</th><th>Seuil</th></tr></thead>
                            <tbody>
                            <c:forEach var="item" items="${lowStockItems}">
                                <tr>
                                    <td><strong><c:out value="${item.name}"/></strong></td>
                                    <td>${item.usableQuantity}</td>
                                    <td>${item.minimumStock}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </article>

        <article class="card">
            <h2>Lots expirant sous 30 jours</h2>
            <c:choose>
                <c:when test="${empty expiringLots}">
                    <p class="muted">Aucun lot concerne.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap compact">
                        <table>
                            <thead><tr><th>Article</th><th>Quantite</th><th>Expiration</th></tr></thead>
                            <tbody>
                            <c:forEach var="lot" items="${expiringLots}">
                                <tr>
                                    <td><strong><c:out value="${lot.itemName}"/></strong></td>
                                    <td>${lot.quantity}</td>
                                    <td><c:out value="${lot.expirationDisplay}"/></td>
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
        <h2>Commandes fournisseur</h2>
        <p>Une commande en attente peut etre receptionnee avec une date d'expiration commune aux lignes.</p>
    </div>
    <div class="table-wrap">
        <table>
            <thead>
            <tr><th>No</th><th>Date</th><th>Fournisseur</th><th>Articles</th><th>Statut</th><th>Reception</th></tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty orders}">
                    <tr><td colspan="6" class="muted center">Aucune commande.</td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td><strong>#${order.id}</strong></td>
                            <td><c:out value="${order.orderDateDisplay}"/></td>
                            <td><c:out value="${order.supplierName}"/></td>
                            <td>
                                <c:forEach var="line" items="${order.lines}" varStatus="loop">
                                    <c:out value="${line.itemName}"/> (${line.quantity})<c:if test="${!loop.last}">, </c:if>
                                </c:forEach>
                            </td>
                            <td>
                                <span class="pill ${order.pending ? 'pill-warn' : 'pill-ok'}">
                                    <c:out value="${order.statusLabel}"/>
                                </span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${order.pending}">
                                        <form method="post" action="${ctx}/manager/orders/receive" class="inline-form">
                                            <input type="hidden" name="orderId" value="${order.id}">
                                            <input type="date" name="expiration" required aria-label="Date d'expiration">
                                            <button class="btn btn-sm" type="submit">Receptionner</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="muted">Traitee</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</section>

<section class="section">
    <article class="card">
        <h2>Creer une commande</h2>
        <form method="post" action="${ctx}/manager/orders/create" class="form-grid">
            <label>Fournisseur
                <select name="supplierId" required>
                    <option value="">Choisir un fournisseur</option>
                    <c:forEach var="supplier" items="${suppliers}">
                        <option value="${supplier.id}"><c:out value="${supplier.name}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Article
                <select name="itemId" required>
                    <option value="">Choisir un article</option>
                    <c:forEach var="item" items="${items}">
                        <option value="${item.id}"><c:out value="${item.name}"/></option>
                    </c:forEach>
                </select>
            </label>
            <label>Quantite commandee<input type="number" name="quantity" min="1" value="1" required></label>
            <div><button class="btn" type="submit">Creer la commande</button></div>
        </form>
    </article>
</section>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
