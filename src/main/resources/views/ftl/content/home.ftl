<#-- @ftlvariable name="model" type="org.multibit.site.views.PublicFreemarkerView" -->
<#include "base.ftl">
<html lang="en">
<head>
  <#include "../includes/head.ftl">
</head>
<body>
<#include "../includes/header.ftl">

<div id="container3">
  <div id="back1">
    <div id="back2">
      <div class="wrap">

        <#if alert??>
          <div class="alert-message ${alertClass}">${alertText}</div>
        </#if>
        <div id="content">

          <#-- Pull in the content from the model -->
          ${model.content}

          <#include "../includes/right-sidebar.ftl">

        </div>

        <#include "../includes/footer.ftl">

      </div>
    </div>
  </div>
</div>

<#include "../includes/cdn-scripts.ftl">

<#-- Provide some animation for the screenshots -->
<script type="text/javascript">

  $(function() {
    $(".columns p").blockColumnize({columns:".columns .column"});
    $(".screens").cycle({pause:true,height:430});
    $(".show-older").click(function(ev) {
      ev.preventDefault();
      $(this).next(".older").slideDown();
      $(this).hide()
    });
    $(".older").hide();
  });

</script>

</body>

</html>