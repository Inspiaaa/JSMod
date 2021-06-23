
function _makeCoroutineManager() {
	var runningCoroutinesById = new Map();

	function* _makeIdGenerator() {
		var idCounter = 0

		while (true) {
			yield idCounter++
		}
	}
	var _idGenerator = _makeIdGenerator();

	return {
		createId() {
			return _idGenerator.next().value;
		},

		start(coroutine, id=undefined) {
			if (typeof id === "undefined") {
				id = this.createId();
			}
	
			runningCoroutinesById.set(id, coroutine);
			return id;
		},

		stop(id) {
			runningCoroutinesById.delete(id);
		},

		stopAll() {
			runningCoroutinesById.clear();
		},

		onTick() {
			for (var entry of runningCoroutinesById.entries()) {
				var id, generator, result;

				[id, generator] = entry;

				try {
					result = generator.next();
				}
				catch (error) {
					console.error(error.toString())
					continue;
				}

				if (result.done) {
					runningCoroutinesById.delete(id);
				}
			}
		},

		*waitForTicks(ticks) {
			while (ticks --) yield;
		},

		*waitForSeconds(seconds) {
			var startTime = Time.time();
			var endTime = startTime + seconds;

			while (Time.time() < endTime) {
				yield;
			}
		},

		*waitWhile(func) {
			while (func()) yield;
		},

		*waitUntil(func) {
			while (! func()) yield;
		}
	}
}


const Coroutine = _makeCoroutineManager();
