import { MessageSquare, Star } from "lucide-react";
import { cn } from "@/lib/utils"; 
import { Avatar, AvatarFallback } from "@/components/ui/avatar";

interface FeedbackSectionProps {
    feedbacks: Feedback[]; 
}

export const FeedbackSection = ({ feedbacks }: FeedbackSectionProps) => {
    
    const mediaVoto = feedbacks.length > 0 
        ? feedbacks.reduce((acc, curr) => acc + curr.score, 0) / feedbacks.length 
        : 0;

    return (
        <div className="mt-6 border-t pt-6">
            <div className="flex items-center justify-between mb-4">
                <h3 className="font-semibold flex items-center gap-2 text-lg">
                    <MessageSquare className="h-5 w-5 text-primary" />
                    Recensioni
                </h3>
                
                {feedbacks.length > 0 && (
                    <div className="flex items-center gap-2 bg-primary/5 px-3 py-1 rounded-full">
                        <div className="flex items-center gap-0.5">
                            {[1, 2, 3, 4, 5].map((star) => (
                                <Star
                                    key={star}
                                    className={cn(
                                        "h-3.5 w-3.5",
                                        star <= Math.round(mediaVoto)
                                            ? "fill-amber-400 text-amber-400"
                                            : "text-muted-foreground/30"
                                    )}
                                />
                            ))}
                        </div>
                        <span className="text-sm font-bold">{mediaVoto.toFixed(1)}</span>
                        <span className="text-xs text-muted-foreground">
                            ({feedbacks.length})
                        </span>
                    </div>
                )}
            </div>

            {feedbacks.length === 0 ? (
                <div className="bg-muted/10 border border-dashed rounded-xl p-8 text-center">
                    <p className="text-sm text-muted-foreground italic">
                        Nessuna recensione ancora per questa abitazione. 
                        Sii il primo a soggiornare!
                    </p>
                </div>
            ) : (
                <div className="space-y-4 max-h-100 overflow-y-auto pr-2 custom-scrollbar">
                    {feedbacks.map((recensione) => (
                        <div
                            key={recensione.id}
                            className="p-4 rounded-xl border border-border bg-card shadow-sm hover:shadow-md transition-shadow"
                        >
                            <div className="flex items-start justify-between gap-2 mb-3">
                                <div className="flex items-center gap-3">
                                    <Avatar className="h-9 w-9 border">
                                        <AvatarFallback className="text-xs bg-primary/10 text-primary font-bold">
                                            {recensione.user.name[0]}{recensione.user.lastName[0]}
                                        </AvatarFallback>
                                    </Avatar>
                                    <div>
                                        <p className="text-sm font-semibold">
                                            {recensione.user.name} {recensione.user.lastName}
                                        </p>
                                        <p className="text-[10px] text-muted-foreground uppercase tracking-wider font-medium">
                                            Soggiorno verificato
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-0.5">
                                    {[1, 2, 3, 4, 5].map((star) => (
                                        <Star
                                            key={star}
                                            className={cn(
                                                "h-3 w-3",
                                                star <= recensione.score
                                                    ? "fill-amber-400 text-amber-400"
                                                    : "text-muted-foreground/30"
                                            )}
                                        />
                                    ))}
                                </div>
                            </div>
                            
                            <h4 className="text-sm font-bold mb-1">{recensione.title}</h4>
                            <p className="text-sm text-muted-foreground leading-relaxed">
                                {recensione.text}
                            </p>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};