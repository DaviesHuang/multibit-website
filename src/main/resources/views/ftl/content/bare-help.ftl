<#-- @ftlvariable name="model" type="org.multibit.site.views.PublicFreemarkerView" -->
<#-- Template for the MultiBit client help pages (no extra formatting) -->
<#include "base.ftl">
<#-- Required for IE to render correctly -->
<!DOCTYPE HTML>
<html lang="en">
<head>

<style>
  .alert-message {
    background-color: #FFF3DE;
    border: 1px solid #A78241;
    margin: 10px 0px;
    padding: 10px;
    color: #000;
  }

</style>
</head>
<body>

<p><a href="help_contents.html">Back to contents</a></p>

<#-- Pull in the content from the model -->
${model.content}

<p><a href="help_contents.html">Back to contents</a></p>

</body>

</html>