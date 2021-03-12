
$ ->
  ws = new WebSocket "ws://"+location.host+$("body").data("ws-url")
  ws.onopen = () ->
  		console.log(event)
  		ws.send(JSON.stringify({starting: true}))
  ws.onmessage = (event) ->
    message = JSON.parse event.data
    switch message.type
      when "unreplied"
        populateUnreplied(message)
      when "stockupdate"
        updateStockChart(message)
      else
        console.log(message)


populateUnreplied = (message) ->
	$("#unreplied .cloned").remove()
	$.each( JSON.parse(message.unmanaged), (i, item) ->
     clonedRow = $("#unreplied #order_ph").clone()
     $("td.ph_id", clonedRow).html('#'+item.id)
     $("td.ph_date", clonedRow).html(item.date)
     $("td.ph_details", clonedRow).html(item.brand+", "+item.model+", "+item.city)
     $("td.ph_name", clonedRow).html(item.managedBy)
     $(clonedRow).attr("id", "#order_"+item.id)
     $(clonedRow).addClass("cloned")
     $(clonedRow).removeClass("hidden")
     clonedRow.appendTo("#unreplied tbody"))
	console.log($("#unreplied").html())
