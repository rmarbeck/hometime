
track_link = (category, action, label, value) ->
    if value is ''
        if label is ''
            ga('send', 'event', category, action)
        else
            ga('send', 'event', category, action, label)
    else
        ga('send', 'event', category, action, label, value)


follow_link = (target) ->
    url = $(target).attr('href')
    if $(target).attr('target') is '_blank'
        window.open(url)
    else
        window.location = url

get_targeted_a = (event) ->
    target = event.target
    while (target && target.nodeName isnt 'A')
        target = target.parentNode
    target

$('.tracked').click (event) ->
    event.preventDefault()
    target = get_targeted_a(event)
    try
        category = $(target).attr("data-category")
        action = $(target).attr("data-action")
        label = $(target).attr("data-label")
        value = $(target).attr("data-value")
        track_link(category, action, label, value)
    catch error
        
    follow_link(target)

