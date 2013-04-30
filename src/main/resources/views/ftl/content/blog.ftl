<#-- @ftlvariable name="model" type="org.multibit.site.views.PublicFreemarkerView" -->
<#-- Template for the blog pages with no download promotion -->
<#include "base.ftl">
<#-- Required for IE to render correctly -->
<!DOCTYPE HTML>
<html lang="en">
<head>
  <#include "../includes/head.ftl">
</head>
<body>
<#include "../includes/header.ftl">

<div id="container3">
      <div class="wrap">

        <#if alertText??>
          <div class="alert-message ${alertClass}">${alertText}</div>
        </#if>
        <div id="content-full">

          <#-- Pull in the content from the model -->
        ${model.content?replace("downloadVersion","${downloadVersion}")}

        </div>

        <#include "../includes/footer.ftl">

  </div>
</div>

<#include "../includes/cdn-scripts.ftl">

</body>

</html>
