
function AutoBridge() {
    return {
        name: "AutoBridge",
        activeByDefault: true,
        onTick: () => {
            if (Input.isMouseDown(4)) {
                var pos = Player.getPos()
                pos[1] -= 1
                Chat.msg(JSON.stringify(World.getBlockState(pos)))
                World.setBlockState(pos, {"facing": "east"})
                //if (World.getBlock(pos) == Blocks.AIR)
                //    World.setBlock(pos, Blocks.STONE)
            }
        }
    }
}
Engine.registerModule( AutoBridge )

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


function FullBright() {
    var normalGamma;

    return {
        name: "FullBright",
		activationKey: KEY_B,
		saveState: () => normalGamma,
		loadState: state => {normalGamma = state},
        onActivate: () => {
            normalGamma = Rendering.getGamma()
            Rendering.setGamma(100.0)
        },
        onDeactivate: () => {
            Rendering.setGamma(normalGamma)
        }
    }
}

Engine.registerModule( FullBright )


function Zoom() {
	var normalFOV;

	return {
		name: "Zoom",
		activationKey: KEY_Z,
		saveState: () => normalFOV,
		loadState: state => {normalFOV = state},
		onActivate: () => {
			normalFOV = Rendering.getFOV()
			Rendering.setFOV(20)
		},
		onDeactivate: () => {
			Rendering.setFOV(normalFOV)
		}
	}
}

Engine.registerModule( Zoom )
