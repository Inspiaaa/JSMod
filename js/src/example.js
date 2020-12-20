
function AutoClicker() {
    var lastClick = 0;

    return {
        name: "AutoClicker",
        desc: "Automatically clicks",
		activationKey: KEY_X,
		activeByDefault: true,
        onTick: () => {
            if (Input.isMouseDown(3)) {
                if (lastClick > 2) {
					Player.leftClick()
					lastClick = 0
                }
        
                lastClick ++
            }
        }
    }
}

Engine.registerModule( AutoClicker )


function AutoSprint() {
	return {
		name: "AutoSprint",
		desc: "Automatically sprints when you move forwards",
		activeByDefault: true,
		onTick: () => {
			if (Input.isKeyDown(KEY_W)) {
				KeyBind.sprint.setState(true);
			}
		}
	}
}

Engine.registerModule( AutoSprint )


function ToggleSneak() {
	return {
		name: "ToggleSneak",
		activationKey: KEY_C,
		onTick: () => {
			KeyBind.sneak.setState(true)
		},
		onDeactivate: () => {
			KeyBind.sneak.update()
		}
	}
}

Engine.registerModule( ToggleSneak )
