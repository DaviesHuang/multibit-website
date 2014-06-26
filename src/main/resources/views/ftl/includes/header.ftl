<#-- @ftlvariable name="" type="org.multibit.site.views.PublicFreemarkerView" -->
<div class="navbar-wrapper">
  <div class="container">
    <div class="page-header">
      <h1><img class="header-logo" src="/images/clients/MultiBitHD-logo.svg">MultiBit HD</h1>
    </div>
    <div class="navbar navbar-inverse navbar-static-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="/index.html">MultiBit</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
          ${model.navBar}
          </ul>
          <ul class="nav navbar-nav navbar-right">
            <li>
              <div id="karmaAds" class="hidden-sm navbar-right">
                <iframe scrolling="no" frameBorder="0" src="/ad"></iframe>
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <#-- Alert bar -->
    <#if alertText??>
    <div class="container">
      <div class="alert alert-${alertClass}">
      ${alertText}
      </div>
    </div>
    </#if>
  </div>
</div>
