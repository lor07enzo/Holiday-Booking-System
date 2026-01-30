
import { toast } from "sonner";

const API_URL = import.meta.env.VITE_API_URL;

function Homepage() {


    const loadBookings = async () => {
        try {
            const res = await fetch(`${API_URL}/api/v1/bookings`);
            if (!res.ok) {
                throw new Error("Failed to fetch bookings");
            } 
            return res.json();
           
        } catch (error) {
            console.error(error);
            toast.error("Error server comunication!");
        }
    }
    loadBookings();

    return(
        <div className="w-full h-screen flex flex-col items-center justify-start pt-5 gap-4">
            <div className="flex flex-col p-6 item-center w-full justify-center">

                
                {/* <div className="w-full max-w-4xl">
                    <div className="flex items-center w-full border rounded-lg shadow-xl shadow-black/20 px-2">

                        <div className="flex items-center gap-2 py-2 w-full">
                            <Hotel className="shrink-0"/>
                            <div className="flex flex-col min-w-0">
                                <span className="text-xs text-muted-foreground leading-tight">Select your Destination</span>
                                <input 
                                    type="text" 
                                    placeholder="Insert name of habitation" 
                                    className="w-full min-w-0 bg-transparent border-0 p-0 text-sm placeholder:text-muted-foreground focus:outline-none"
                                />
                            </div>
                        </div>

                        <Separator aria-orientation="vertical" className="border mx-2 h-8"/>

                        <Button variant="ghost" className="pl-2 pr-6 h-full">
                            <span className="flex items-center gap-2">
                                <CalendarDays/>
                                <div className="flex flex-col items-start leading-tight">
                                    <span className="text-xs text-muted-foreground">Start / End</span>
                                    <span>DateStart - DateEnd</span>
                                </div>
                            </span>
                        </Button>

                        <Separator aria-orientation="vertical" className="border mx-2 h-8"/>

                        <Button variant="ghost" className="pl-2 pr-6 h-full">
                            <span className="flex items-center gap-2">
                                <Users/>
                                <div className="flex flex-col items-start leading-tight">
                                    <span className="text-xs text-muted-foreground">Guests and Rooms</span>
                                    <span>1 Guest, 1 Room</span>
                                </div>
                            </span>
                        </Button>

                        <Button className="ml-2 h-full px-4"> 
                            <Search className="mr-2 h-4 w-4"/> Search
                        </Button>
                    </div>
                </div> */}
            </div>
        </div>
    )
}
export default Homepage