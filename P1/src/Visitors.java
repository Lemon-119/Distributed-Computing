//Name: Yifei Du
public class Visitors implements Runnable{
	private Monitor mo= null;
	private String name;
	private int id = 0;
	private long system_start_time = 0;

	public Visitors(String name, int id, long time, Monitor mo) {
	      this.name = name;
	      this.id = id;
	      this.system_start_time = time;
	      this.mo = mo;
	      new Thread(this).start();
	}
	
	public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "] " + name + id +": " + m);
    }
	
	public void run() {
		
			//here, first I check the theater if is closed. In worst case, may be all movie is over, but some visitor still try go in theater.
			if(mo.theaterIsOpen()) {
				try {
					Thread.sleep(1000 + (int)(1+Math.random()*(1000)));
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				msg("arrives at theater.");
				mo.goInTheatre(id);
				mo.gatherIntoGroups(id);
				mo.goOutTheater(id);
			}
			
			try {
				Thread.sleep(200 + (int)(1+Math.random()*(100)));
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// if theater is not closed, then 75%, visitor will watch movie again.
			if((int)(1+Math.random()*(100)) <= 75 && mo.theaterIsOpen()) {
				msg("is so impressed with the presentation, want to see movie again!");
				mo.goInTheatre(id);
				mo.gatherIntoGroups(id);
				mo.goOutTheater(id);
			}

	}

}